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
				DiningPhilosophers dp;
				PhilosopherRunnable[] runnables = new PhilosopherRunnable[LIMIT];
        Semaphore[] chopsticks = new Semaphore[LIMIT];

        for(int i = 0; i < LIMIT; i++)
            chopsticks[i] = new Semaphore(1);

        Philosopher[] philosophers = new Philosopher[LIMIT];

        for(int i = 0; i < LIMIT; i++) {
            philosophers[i] = new Philosopher(i, chopsticks[i], chopsticks[(i + 1) % LIMIT]);
            //new Thread(philosophers[i]).start();
        }

				dp = new DiningPhilosophers(philosophers);

				for(int i = 0; i < LIMIT; i++) {
					runnables[i] = new PhilosopherRunnable(dp, i);
					new Thread(runnables[i]).start();
				}
    }
}

class Philosopher 
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

    
    public void releaseChopsticks() throws InterruptedException
	{
		leftChopstick.release();
		rightChopstick.release();
		System.out.println("Philosopher " + id + " released its left and right chopsticks.\n");
    }
}

class PhilosopherRunnable implements Runnable
{
	private int id;
	private DiningPhilosophers dp;
	private Random randomizer; 

	public PhilosopherRunnable(DiningPhilosophers dp, int id)
	{
		this.id = id;
		this.dp = dp;
		randomizer = new Random();
	}
	
	public void run() 
	{
        try
		{
            while (true) 
			{
                think();
                dp.pickup(id);
                eat();
                dp.putdown(id);
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
			Thread.sleep(randomizer.nextInt(2000));
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
			Thread.sleep(randomizer.nextInt(2000));
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
    }
}

class DiningPhilosophers
{
	enum State{THINKING, HUNGRY, EATING};
	private State[] states;
	private Philosopher[] philos;
	private Semaphore[] self;

	public DiningPhilosophers(Philosopher[] philos)
	{
		this.philos = philos;
		this.states = new State[5];
		this.self = new Semaphore[5];
		for(int i = 0; i < 5; i++)
			states[i] = State.THINKING;
		for(int i = 0; i < 5; i++)
			self[i] = new Semaphore(1);
	}

	void pickup(int i)
	{
		states[i] = State.HUNGRY;
		if(test(i) == 1)
		{
			try{
				philos[i].acquireChopsticks();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

		if(states[i] != State.EATING)
		{
			try{
				self[i].acquire();
				philos[i].acquireChopsticks();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

	}

	void putdown(int i)
	{
		states[i] = State.THINKING;
		try{
			philos[i].releaseChopsticks();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		test((i + 4) % 5);
		test((i + 1) % 5);
	}

	int test(int i)
	{
		boolean left = states[(i + 4) % 5] != State.EATING;
		boolean right = states[(i + 1) % 5] != State.EATING;
		boolean center = states[i] == State.HUNGRY;

		if(left && right && center)
		{
			states[i] = State.EATING;
			self[i].release();
			return 1;
		}
		return 0;
	}
}
