/*
	CSOPESY S15
	
	Kenneth Oafallas
	Darren Tan
	
	Java Compilation: set path=C:\Program Files\Java\jdk-10.0.2\bin
*/

import java.util.Random;
import java.util.concurrent.Semaphore;


public class Activity2 
{
    public static int LIMIT = 5;

    public static void main(String[] args) 
	{
        Semaphore[] chopsticks = new Semaphore[LIMIT];

        for(int i = 0; i < LIMIT; i++)
            chopsticks[i] = new Semaphore(1);

        Philosopher[] philosophers = new Philosopher[LIMIT];

        for(int i = 0; i < LIMIT; i++) {
            philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % LIMIT]);
            new Thread(philosophers[i]).start();
        }
    }
}

class Philosopher implements Runnable 
{
    private int id;
    private Semaphore leftChopstick;
    private Semaphore rightChopstick;
	private Random randomizer; 


    public Philosopher(int id, Semaphore leftChopstick, Semaphore rightChopstick) 
	{
        this.id = id;
        this.leftChopstick = leftChopstick;
        this.rightChopstick = rightChopstick;
		randomizer = new Random();
    }


    public void run() 
	{
        try
		{
            while (true) 
			{
                think();
                acquireChopsticks();
                eat();
                releaseChopsticks();
            }
		}
		catch(InterruptedException e)
		{
			System.out.println("INTERRUPTION: Philosopher " + id + " is interrupted.\n");
			e.printStackTrace();
		}
    }


    public void think() throws InterruptedException
	{
		try
		{
			System.out.println("Philosopher " + id + " is thinking.\n");
			System.out.flush();
			Thread.sleep(randomizer.nextInt(1000));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
    }

	
	
    public void acquireChopsticks() throws InterruptedException
	{
		try
		{
			leftChopstick.acquire();
			rightChopstick.acquire();
			System.out.println("Philosopher " + id + " acquired its left and right chopsticks.\n");
			System.out.flush();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	
    public void eat()
	{
		try
		{
			System.out.println("Philosopher " + id + " is eating.\n");
			System.out.flush();
			Thread.sleep(randomizer.nextInt(1000));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
    }

    
    public void releaseChopsticks() throws InterruptedException
	{
		leftChopstick.release();
		rightChopstick.release();
		System.out.println("Philosopher " + id + " released its left and right chopsticks.\n");
    }
}