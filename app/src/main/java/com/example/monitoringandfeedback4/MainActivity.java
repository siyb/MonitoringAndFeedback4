package com.example.monitoringandfeedback4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView resultView;
    // Using Looper.getMainLooper() we are connecting this Handler to
    // the Looper of the Main Thread.
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText input = findViewById(R.id.number_input);
        resultView = findViewById(R.id.result);
        // Adding a click action
        findViewById(R.id.check_prime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(
                        new PrimeRunnable(Long.parseLong(input.getText().toString()))
                );
                System.err.println("MAIN " + Thread.currentThread().getName());
                // start the thread, do not use run()
                t.start();
            }
        });
    }


    private class PrimeRunnable implements Runnable {
        private long number;

        // No access modifier === package private
        PrimeRunnable(long number) {
            this.number = number;
        }

        @Override
        public void run() {
            final Pair<Boolean, Long> result = isPrime(number);
            // Runs in a Worker Thread
            System.err.println("WORKER " + Thread.currentThread().getName());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // Runs on Main (UI) Thread
                    System.err.println("HANDLER " + Thread.currentThread().getName());
                    // This String concatination is dirty but we can still use it in our example
                    resultView.setText(result.first + " " + result.second + " ms");
                    resultView.setError("THIS DOES WORK!");
                }
            });
        }

        private Pair<Boolean, Long> isPrime(long number) {
            long start = System.currentTimeMillis();
            //int num = 2,000,083;
            boolean isPrime = true;
            for (int divisor = 2; divisor <= number / 2; divisor++) {
                try {
                    // lets add some idle time to make the calculation
                    // last longer.
                    Thread.sleep(1l);
                } catch (Throwable tr) {

                }
                if (number % divisor == 0) {
                    isPrime = false;
                    return new Pair<>(isPrime, System.currentTimeMillis() - start);
                }
            }
            return new Pair<>(isPrime, System.currentTimeMillis() - start);
        }
    }
}
