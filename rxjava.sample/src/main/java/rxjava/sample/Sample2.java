package rxjava.sample;

import static java.lang.Thread.currentThread;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Sample2 {
	
	public static void main(String[] args) {
//		testJ7();
//		testJ8();
//		testComputationScheduler();
//		testComputationScheduler2();
//		testChainedScheduler();
		testChainedObserver();
		
//		testOddEven();
	}
	
	private static void println(String caseName, String str) {
		System.out.println("[" + currentThread().getName() + "] " + caseName + ":\t" + str);
	}

	/**
	 * 
	 */
	private static void testJ7() {
		String caseName	= "Java7";
		Observable.range(1, 5)
        .doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                println(caseName, "Emitting item on: " + currentThread().getName());
            }
        })
        .map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(@NonNull Integer integer) throws Exception {
            	println(caseName, "Processing item on: " + currentThread().getName());
                return integer * 2;
            }
        })
        .subscribeWith(new DisposableObserver<Integer>() {
            @Override
            public void onNext(@NonNull Integer integer) {
            	println(caseName, "Consuming item on: " + currentThread().getName());
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
	}
	
	/**
	 * 
	 */
	private static void testJ8() {
		String caseName	= "Java8";
		Observable.range(10, 5)
	        .doOnNext(integer -> println(caseName, "Emitting item on: " + currentThread().getName()))
	        .map(integer -> {
	        	println(caseName, "Processing item on: " + currentThread().getName());
	        	return integer * 2;
	        })
	        .subscribe(integer -> println(caseName, "Consuming item on: " + currentThread().getName()));
	}

	/**
	 * 
	 */
	private static void testComputationScheduler() {
		String caseName	= "SchComp";
		Observable.range(100, 5)
        	.subscribeOn(Schedulers.computation())
        	.doOnNext(integer -> println(caseName, "Emitting item " + integer ))
        	.subscribe(integer -> println(caseName, "Consuming item " + integer ));
	}
	
	/**
	 * 
	 */
	private static void testComputationScheduler2() {
		String caseName	= "SchComp2";
		Observable.range(1, 5)
			.subscribeOn(Schedulers.newThread())
			.doOnNext(integer -> println(caseName, "Emitting item " + integer ))
	        .map(integer -> integer * 3)
	        .filter(integer -> integer % 2 == 0)
	        .blockingSubscribe(integer -> println(caseName, "Consuming item " + integer ));
	}
	
	/**
	 * 
	 */
	private static void testChainedScheduler() {
		String caseName	= "SchChain";
		Observable.range(1, 5)
			.subscribeOn(Schedulers.io())
			.subscribeOn(Schedulers.trampoline())	// this is ignored
	        .doOnNext(integer -> println(caseName, "Emitting item " + integer ))
	        .map(integer -> integer * 3)
	        .filter(integer -> integer % 2 == 0)
	        .blockingSubscribe(integer -> println(caseName, "Consuming item " + integer ));
	}
	
	/**
	 * 
	 */
	private static void testChainedObserver() {
		String caseName	= "ObsvrChain";
		Observable.range(1, 10)
			.subscribeOn(Schedulers.io())
	        .doOnNext(integer -> println(caseName, "Emitting item " + integer ))
	        .observeOn(Schedulers.computation())
	        .map(integer -> {
	            println(caseName, "Mapping item " + integer );
	            return integer * integer;
	        })
	        .observeOn(Schedulers.newThread())
	        .filter(integer -> {
	            println(caseName, "Filtering item " + integer );
	            return integer % 2 == 0;
	        })
	        .blockingSubscribe(integer -> println(caseName, "Consuming item " + integer ));
	}
	
	/**
	 * 
	 */
	private static void testOddEven() {
		String caseName	= "OddEven";
		int n	= 10;
		Flowable<Integer> even = Flowable.range(1, n)
									.delay(2, TimeUnit.SECONDS)
									.observeOn(Schedulers.io())
									.filter(i -> i % 2 == 0);
		
		Flowable<Integer> odd = Flowable.range(1, n)
									.delay(1, TimeUnit.SECONDS)
									.observeOn(Schedulers.io())
									.filter(i -> i % 2 == 1);
		
		
		Flowable.merge(odd, even)
				.blockingSubscribe(num -> println(caseName, "" + num));
		
		/*Observable.range(1, 10)
			.subscribeOn(Schedulers.io())
	        .doOnNext(integer -> println(caseName, "Emitting item " + integer ))
	        .observeOn(Schedulers.computation())
	        .map(integer -> {
	            println(caseName, "Mapping item " + integer );
	            return integer * integer;
	        })
	        .observeOn(Schedulers.newThread())
	        .filter(integer -> {
	            println(caseName, "Filtering item " + integer );
	            return integer % 2 == 0;
	        })
	        .blockingSubscribe(integer -> println(caseName, "Consuming item " + integer ));*/
	}

}
