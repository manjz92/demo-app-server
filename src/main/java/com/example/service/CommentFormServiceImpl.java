package com.example.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.CommentForm;
import com.example.dto.CommentFormSaveDto;
import com.example.exception.CommentFormException;
import com.example.repository.CommentFormRepository;

@Service
public class CommentFormServiceImpl implements CommentFormService {

	static Logger logger = LoggerFactory.getLogger(CommentFormServiceImpl.class);

	@Autowired
	private CommentFormRepository commentFormRepository;

	@Override
	public String saveFormData(CommentFormSaveDto saveData) {
		logger.info("Entered save comment method -> {} ", saveData);
		try {
			validateSaveData(saveData);

			List<String> wordsList = Arrays.asList(saveData.getComment().split(" "));

			File file = new File(getClass().getClassLoader().getResource("objectionable_words").getFile());

			/**
			 * Method to validate objectionable words
			 */
			validateObjectionableWords(wordsList, file, saveData);

			return "SUCCESS";
		} catch (Exception e) {
			throw new CommentFormException(e.getMessage());
		}

	}

	private void validateObjectionableWords(List<String> wordsList, File file, CommentFormSaveDto dtoData) {
		try (Scanner sc = new Scanner(file)) {
			StringBuilder str = new StringBuilder();
			boolean foulWordFound = false;
			while (sc.hasNextLine()) {
				if (wordsList.stream().anyMatch(word -> word.equalsIgnoreCase(sc.nextLine()))) {
					str.append(" " + sc.nextLine());
					foulWordFound = true;
				}
			}
			if (foulWordFound) {
				logger.error("Exception thrown due to foul language used {} ", str);
				throw new CommentFormException("You cannot use foul words like" + str.toString() + " in the comment");
			} else {
				CommentForm saveData = new CommentForm();
				BeanUtils.copyProperties(dtoData, saveData);
				commentFormRepository.save(saveData);
			}

		} catch (FileNotFoundException e) {
			logger.error("Exception occured due to file not found {} ", e.getMessage());
		} catch (Exception e) {
			throw new CommentFormException(e.getMessage());
		}
	}

	private void validateSaveData(CommentFormSaveDto saveData) {

		Objects.requireNonNull("Comment field cannot be null", saveData.getComment());
		Objects.requireNonNull("Name field cannot be null", saveData.getName());
		Objects.requireNonNull("Email field cannot be null", saveData.getEmail());

	}

}
