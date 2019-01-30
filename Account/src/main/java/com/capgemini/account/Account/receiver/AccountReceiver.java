package com.capgemini.account.Account.receiver;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.capgemini.account.Account.resource.AccountResource;
import com.capgemini.transaction.transactionservice.entity.Transaction;

@Component
public class AccountReceiver {
	
	@Autowired
	private AccountResource accountResource;

	@Bean
	public Queue queue() {
		return new Queue("DepositeQueue", false);
	}

	/*
	 * @Bean public Jackson2JsonMessageConverter converter() { return new
	 * Jackson2JsonMessageConverter(); }
	 */
	 
	 
	@RabbitListener(queues = "DepositeQueue")
	public void processMessage(Transaction transaction) {
		System.out.println("Inside Reciever");
		accountResource.updateAccountBalance(transaction);
	}

	

}

