package rxjava.sample;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class Sample1 {

	public static void main(String[] args) {
		List<String> words = Arrays.asList(
				 "the",
				 "quick",
				 "brown",
				 "fox",
				 "jumps",
				 "over",
				 "the",
				 "lazy",
				 "dog"
				);

		Observable<String> observer = Observable.fromIterable(words) // provides data
												.flatMap(word -> Observable.fromArray(word.split("")))
												.distinct()
												.sorted()
												.zipWith(Observable.range(100, 1000), 
														(string, count)->String.format("%3d. %s", count, string));
        
        
        Observable<String> observer2 = Observable.fromIterable(words)
        										.zipWith(Observable.range(1, 1000), 
        												(string, count)->String.format("%3d. %s", count, string));
        
        Observable.merge(observer2, observer)
        	.subscribe(System.out::println);
        
	}

}
