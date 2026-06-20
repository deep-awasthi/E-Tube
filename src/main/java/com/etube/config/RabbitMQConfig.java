package com.etube.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "etube.exchange";
    
    public static final String UPLOAD_QUEUE = "video.uploads";
    public static final String UPLOAD_ROUTING_KEY = "video.upload.key";

    public static final String VIEW_QUEUE = "video.views";
    public static final String VIEW_ROUTING_KEY = "video.view.key";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue uploadQueue() {
        return QueueBuilder.durable(UPLOAD_QUEUE).build();
    }

    @Bean
    public Queue viewQueue() {
        return QueueBuilder.durable(VIEW_QUEUE).build();
    }

    @Bean
    public Binding bindingUpload(Queue uploadQueue, TopicExchange exchange) {
        return BindingBuilder.bind(uploadQueue).to(exchange).with(UPLOAD_ROUTING_KEY);
    }

    @Bean
    public Binding bindingView(Queue viewQueue, TopicExchange exchange) {
        return BindingBuilder.bind(viewQueue).to(exchange).with(VIEW_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
