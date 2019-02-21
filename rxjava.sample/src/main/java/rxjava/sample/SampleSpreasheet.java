package rxjava.sample;

import static java.lang.Thread.currentThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SampleSpreasheet {
	private static int MIN	= 1;
	private static int MAX	= 100;
	private static int MAX_DELAY	= 3 * 1000;
	private static String EOL		= System.lineSeparator();
	
	public static void main(String[] args) {
		printHeader();
		reactiveSum(createObservableFromEvent("cell-A", 10), createObservableFromEvent("cell-B", 10));
		printFooter();
	}
	
	private static void printHeader() {
		System.out.println("-------------------------- Start ------------------------" + EOL);
	}

	private static void printFooter() {
		System.out.println(EOL + "-------------------------- End ------------------------" + EOL);
	}

	/**
	 * Random number generator with delay 
	 * 
	 * This class mimics random update events to a Cell holding number
	 *
	 */
	private static class RandomNumberWithDelay implements Callable<Integer> {
		private String identifier;
		private int maxDelay;
		private int min;
		private int max;
		
		public RandomNumberWithDelay(String identifier, int min, int max, int maxDelay) {
			super();
			this.identifier	= identifier;
			this.min = min;
			this.max = max;
			this.maxDelay = maxDelay;
		}

		@Override
		public Integer call() throws Exception {
			Random random	= new Random();
			int rNum		= random.nextInt(max - min);
			int rDelay		= random.nextInt(maxDelay);
			
			Thread.sleep(rDelay);

			println(identifier, "" + (min + rNum));
			return Integer.valueOf(min + rNum);
		}
		
	}

	/**
	 * 
	 * @param name
	 * @param count
	 * @return
	 */
	private static Observable<Integer> createObservableFromEvent(String name, int count) {
		ExecutorService executor		= Executors.newSingleThreadExecutor();
		List<Future<Integer>> events	= new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			events.add(executor.submit(new RandomNumberWithDelay(name, MIN, MAX, MAX_DELAY)));
		}
		executor.shutdown();
		
		return Observable.create(new ObservableOnSubscribe<Integer>() {	// Create an Reactive Stream of events

			@Override
			public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
				try {
					emitter.onNext(0);						// Initial Cell Value
					for (Future<Integer> event : events) {	// Update events happening over time
						Integer number	= event.get();
						emitter.onNext(number);				// Signal new event
					}
					
					emitter.onComplete();					// Signal event stream completion
				} catch (Exception e) {
					emitter.onError(e);						// Signal event stream error
				}
			}
		}).subscribeOn(Schedulers.io());					// Event is
	}

	/**
	 * 
	 * @param observableA
	 * @param observableB
	 */
	private static void reactiveSum(Observable<Integer> observableA, Observable<Integer> observableB) {	
		Observable.combineLatest(observableA, observableB, (a, b) -> {		// Observe cell value change
						println("Value updated", "a = " + a + ", b = " + b);// Actual Function is applied here
						return a + b;
					})
					.blockingSubscribe(num -> println("Sum", "" + num));	// Subscribe happens here
	}

	/**
	 * 
	 * @param action
	 * @param str
	 */
	private static void println(String action, String str) {
		System.out.println("[" + currentThread().getName() + "] " + action + ":\t" + str);
	}
}
