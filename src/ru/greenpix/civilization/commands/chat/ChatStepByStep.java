package ru.greenpix.civilization.commands.chat;

import ru.greenpix.civilization.player.ChatExecutor;
import ru.greenpix.civilization.player.CivPlayer;
import ru.greenpix.developer.utils.protocol.title.Title;

public abstract class ChatStepByStep implements ChatExecutor {

	private Step[] steps;
	
	private StepMessage[] messages;
	
	private String[] results;
	
	private int step = -1;
	
	public ChatStepByStep setSteps(Step... steps) {
		this.steps = steps;
		return this;
	}
	
	public ChatStepByStep setMessages(StepMessage... messages) {
		this.messages = messages;
		return this;
	}
	
	public ChatStepByStep setMessages(String... messages) {
		this.messages = new StepMessage[messages.length];
		for(int i = 0; i < messages.length; i++) {
			final int fi = i;
			this.messages[i] = p -> messages[fi];
		}
		return this;
	}
	
	public abstract void onComplete(CivPlayer player);
	
	public String[] getResults() {
		return results;
	}
	
	public ChatExecutor send(CivPlayer p) {
		results = new String[steps.length];
		p.setChatExecutor(this);
		next(p);
		return this;
	}
	
	public void cancel(CivPlayer p) {
		p.sendMessage("&cДействие было отменено!");
		p.setChatExecutor(null);
	}
	
	public void execute(CivPlayer p, String args) {
		if(getStep().run(p, args)) {
			results[step] = args;
			if(!next(p)) {
				p.setChatExecutor(null);
				onComplete(p);
			}
		}
	}
	
	public Step getStep() {
		return steps[step];
	}
	
	public boolean next(CivPlayer p) {
		step++;
		if(step >= steps.length) return false;
		Title.sendTitles(p.toBukkit(), "", "&6&lШаг " + (step + 1) + " из " + steps.length, 20, 60, 20);
		p.sendMessage("&7=-=-=-=-=-=-=-=-=->>> &6&lШаг " + (step + 1) + " из " + steps.length);
		p.sendMessage(messages[step].get(p));
		return true;
	}
	
	public interface Step {
		
		public boolean run(CivPlayer p, String args);
		
	}
	
	public interface StepMessage {
		
		public String get(CivPlayer p);
		
	}
	
}
