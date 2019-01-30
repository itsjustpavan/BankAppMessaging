package com.moneymoney.web.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.moneymoney.web.entity.CurrentDataSet;
import com.moneymoney.web.entity.Transaction;

@Controller
public class BankAppController {

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/")
	public String indexForm() {
		return "index";
	}

	@RequestMapping("/withdraw")
	public String withdrawForm() {
		return "WithdrawForm";
	}

	@RequestMapping("/deposit")
	public String depositForm() {
		return "DepositForm";
	}

	@RequestMapping("/depositMethod")
	public String deposit(@ModelAttribute Transaction transaction, Model model) {
		restTemplate.postForEntity("http://localhost:8989/transactions", transaction, null);
		model.addAttribute("message", "Success!");
		return "DepositForm";
	}

	@RequestMapping("/withdrawMethod")
	public String withdraw(@ModelAttribute Transaction transaction, Model model) {
		transaction.setTransactionDetails("Atm");
		restTemplate.postForEntity("http://localhost:8989/transactions/withdraw", transaction, null);
		model.addAttribute("message", "Success!");
		return "WithdrawForm";
	}

	@RequestMapping("/fundtransfer")
	public String fundTransferForm() {

		return "FundTransfer";
	}

	@RequestMapping("/fundTransferForm")
	public String fundTransfer(@RequestParam("senderAccountNumber") int senderAccountNumber,
			@RequestParam("receiverAccountNumber") int receiverAccountNumber, @RequestParam("amount") Double amount,
			@ModelAttribute Transaction transaction, Model model) {
		transaction.setAccountNumber(senderAccountNumber);
		restTemplate.postForEntity("http://localhost:8989/transactions/withdraw", transaction, null);
		transaction.setAccountNumber(receiverAccountNumber);
		restTemplate.postForEntity("http://localhost:8989/transactions", transaction, null);
		model.addAttribute("message", "Success!");
		return "FundTransfer";

	}

	@RequestMapping("/getstatement")
	public ModelAndView getStatementForm(@RequestParam("offset") int offset, @RequestParam("size") int size) {
		CurrentDataSet currentDataSet = restTemplate.getForObject("http://localhost:8989/transactions/statement",
				CurrentDataSet.class);
		int currentSize = size == 0 ? 5 : size;
		int currentOffset = offset == 0 ? 1 : offset;
		Link next = linkTo(methodOn(BankAppController.class).getStatementForm(currentOffset + currentSize, currentSize))
				.withRel("next");
		Link previous = linkTo(
				methodOn(BankAppController.class).getStatementForm(currentOffset - currentSize, currentSize))
						.withRel("previous");
		List<Transaction> transactions = currentDataSet.getTransactions();
		List<Transaction> currentDataSetList = new ArrayList<Transaction>();

		for (int i = currentOffset - 1; i < currentSize + currentOffset - 1; i++) {
			if ((transactions.size() <= i && i > 0) || currentOffset < 1)
				break;
			Transaction transaction = transactions.get(i);
			currentDataSetList.add(transaction);

		}
		CurrentDataSet dataSet = new CurrentDataSet(currentDataSetList, next, previous);
		return new ModelAndView("getStatement", "currentDataSet", dataSet);
	}

}
