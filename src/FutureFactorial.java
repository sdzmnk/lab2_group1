
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class FutureFactorial {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main почав роботу");
        ConcurrentHashMap<Integer, Long> factorials = new ConcurrentHashMap<>();
        factorials.put(3, 0L);
        factorials.put(2, 0L);
        factorials.put(9, 0L);
        factorials.put(7, 0L);

        Iterator<Integer> iterator = factorials.keySet().iterator();

        Callable<Long> callable = () -> {
            if (iterator.hasNext()) {

                int key = iterator.next();
                long result = fact(key);
                factorials.put(key, result);
                Thread.sleep(300);
                return result;
            } else {
                return 0L;
            }
        };


        ExecutorService executorService = Executors.newFixedThreadPool(3);

        List<Future<Long>> futures = new ArrayList<>();
        Future<Long> future;

        while (iterator.hasNext()){
            future = executorService.submit(callable);
            futures.add(future);
            Thread.sleep(200);
            if (!future.isDone()) {
                future.cancel(true);
            }
        }

        for (Future element : futures){
            try {
                if (!element.isCancelled()) {
                    System.out.println("Результат виконання: " + element.get());
                } else {
                    System.err.println("Cancelled");
                }
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.printf("Головний потік %s продовжує свою роботу. \n", Thread.currentThread().getName());

        System.out.println(factorials);

        executorService.shutdown();

        System.out.printf("Головний потік %s завершив роботу. \n", Thread.currentThread().getName());

    }

    private static long fact(int x) {
        if(x < 0) {
            throw new RuntimeException();
        }
        if (x <= 2) {
            return x;
        }
        return x * fact(x - 1);
    }

}
