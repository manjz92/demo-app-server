/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 */

package com.gateway.api;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.bouncycastle.util.Strings;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ResponseBodyFilter extends AbstractGatewayFilterFactory<ResponseBodyFilter.Config>{

	public ResponseBodyFilter()  {
		super(Config.class);
	}

	
	public static class Config{
		//NoOp
	}


	@Override
	public GatewayFilter apply(Config config) {
		return new OrderedGatewayFilter( (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();
			System.out.println("request=" + request.getURI().toString());
			
	        Set<Object> uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
	        String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
	        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
	        URI routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
	        System.out.println("Incoming request " + originalUri + " is routed to id: " + route.getId()
	                + ", uri:" + routeUri);

			return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
				ServerHttpRequest mutatedHttpRequest = getServerHttpRequest(exchange, dataBuffer);
				ServerHttpResponse mutatedHttpResponse = parseHttpResponse(exchange);

				return chain.filter(exchange.mutate().request(mutatedHttpRequest).response(mutatedHttpResponse).build());

			});

	}, -2);
	}

	private ServerHttpRequest getServerHttpRequest(ServerWebExchange exchange, DataBuffer dataBuffer) {

		DataBufferUtils.retain(dataBuffer);
		Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));

		String body = toRaw(cachedFlux);
		String decryptedBody = body;
		byte[] decryptedBodyBytes = decryptedBody.getBytes(StandardCharsets.UTF_8);

		return new ServerHttpRequestDecorator(exchange.getRequest()) {

			@Override
			public HttpHeaders getHeaders(){
				return exchange.getRequest().getHeaders();
			}


			@Override
			public Flux<DataBuffer> getBody() {

				return Flux.just(body).
						map(s -> {
							return new DefaultDataBufferFactory().wrap(decryptedBodyBytes);
						});

			}

		};
	}
		private static String toRaw(Flux<DataBuffer> body) {
			AtomicReference<String> rawRef = new AtomicReference<>();
			body.subscribe(buffer -> {
				byte[] bytes = new byte[buffer.readableByteCount()];
				buffer.read(bytes);
				DataBufferUtils.release(buffer);
				rawRef.set(Strings.fromUTF8ByteArray(bytes));
			});
			return rawRef.get();
		}


	
	private ServerHttpResponse parseHttpResponse(ServerWebExchange exchange) {
		ServerHttpResponse originalResponse = exchange.getResponse();

		return (new ServerHttpResponseDecorator(originalResponse) {

			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

				HttpHeaders httpHeaders = originalResponse.getHeaders();


				ClientResponse clientResponse = prepareClientResponse(body, httpHeaders);

				Mono<String> modifiedBody = extractBody(exchange, clientResponse)
						.switchIfEmpty(Mono.empty());
				System.out.println("modified Body="+ modifiedBody.toString());
				BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);

				CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange,
						exchange.getResponse().getHeaders());

				return bodyInserter.insert(outputMessage, new BodyInserterContext())
						.then(Mono.defer(() -> {
							Mono<DataBuffer> messageBody = updateBody(getDelegate(), outputMessage);
							return getDelegate().writeWith(messageBody);
						}));

			}
			
			private ClientResponse prepareClientResponse(Publisher<? extends DataBuffer> body, HttpHeaders httpHeaders) {
				ClientResponse.Builder builder = ClientResponse.create(exchange.getResponse().getStatusCode(), HandlerStrategies.withDefaults().messageReaders());
				return builder.headers(headers -> headers.putAll(httpHeaders)).body(Flux.from(body)).build();
			}
			
			private Mono<String> extractBody(ServerWebExchange exchange, ClientResponse clientResponse) {



				return clientResponse.bodyToMono(byte[].class)
						.map(bytes -> exchange.getResponse().bufferFactory()
								.wrap(bytes))
						.map(buffer -> prepareClientResponse(Mono.just(buffer),
								exchange.getResponse().getHeaders()))
						.flatMap(response -> response.bodyToMono(String.class));


			}
			
			private Mono<DataBuffer> updateBody(ServerHttpResponse httpResponse,
					CachedBodyOutputMessage message) {

				Mono<DataBuffer> response = DataBufferUtils.join(message.getBody());

				return response;

			}
		}).getDelegate();
	}

}
