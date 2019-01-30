package com.capgemini.transaction.transactionservice.sender;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.capgemini.transaction.transactionservice.entity.Transaction;

@Component
@Lazy
public class TransactionSender {
	@Autowired
	private RabbitMessagingTemplate template;

	@Bean
	public Queue depositeQueue() {
		return new Queue("DepositeQueue", false);
	}

	public void sendCurrentBalance(Transaction transaction) { //
		System.out.println("in sender");
		template.convertAndSend("DepositeQueue", transaction);

	}
}
