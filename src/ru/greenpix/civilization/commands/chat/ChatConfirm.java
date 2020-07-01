package ru.greenpix.civilization.commands.chat;

import ru.greenpix.civilization.commands.chat.ChatStepByStep.Step;
import ru.greenpix.civilization.commands.chat.ChatStepByStep.StepMessage;
import ru.greenpix.civilization.player.ChatExecutor;
import ru.greenpix.civilization.player.CivPlayer;

public class ChatConfirm implements ChatExecutor {

	private final Step step;
	
	private final StepMessage message;
	
	public ChatConfirm(String message, Step step) {
		this.message = p -> message;
		this.step = step;
	}
	
	public ChatConfirm(StepMessage message, Step step) {
		this.message = message;
		this.step = step;
	}
	
	public ChatConfirm(String message) {
		this.message = p -> message;
		this.step = (p,a) -> true;
	}
	
	public ChatConfirm(StepMessage message) {
		this.message = message;
		this.step = (p,a) -> true;
	}
	
	public ChatExecutor send(CivPlayer p) {
		p.setChatExecutor(this);
		p.sendMessage(message.get(p));
		return this;
	}
	
	public void cancel(CivPlayer p) {
		p.sendMessage("&cДействие было отменено!");
		p.setChatExecutor(null);
	}
	
	public Step getStep() {
		return step;
	}
	
	public Step getOther() {
		return (p, arg) -> true;
	}
	
	@Override
	public void execute(CivPlayer p, String args) {
		if(args.equalsIgnoreCase("да") || args.equalsIgnoreCase("yes") || args.equalsIgnoreCase("y")) {
			if(getStep().run(p, args)) p.setChatExecutor(null);
		} else {
			if(getOther().run(p, args)) cancel(p);
		}
	}

}
