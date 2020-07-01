package ru.greenpix.civilization.utils;

import com.boydti.fawe.object.FaweQueue;
import com.boydti.fawe.object.RunnableVal2;
import com.boydti.fawe.object.FaweQueue.ProgressType;

public class FaweUtils {

	public static void setProgressTracker(FaweQueue queue, RunnableProgress task) {
		queue.setProgressTracker(new RunnableVal2<ProgressType, Integer>() {
			
			@Override
			public void run(ProgressType type, Integer amount) {
				task.run(type, amount);
			}
			
		});
	}
	
	public interface RunnableProgress {
		
		public void run(ProgressType type, int amount);
		
	}
	
}
