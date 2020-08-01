import java.util.*;

// �� ���μ����� �ʿ��� ������ �����ϴ� Ŭ����
// >> �����ϴ� ���μ����� �����Դϴ�.
class Process {
	private String	name;		// ���μ��� �̸�
	private int 	serviceTime;	// ���μ��� ���񽺽ð�, ����ð� �ؾ� �� �� �ð�
	private int	 	executionTime;	// ���μ����� ������� ����� �ð�
	private int arrivalTime;		// ���μ��� �����ð� 
	
	Process(String name, int arrivalTime, int serviceTime) { // ���μ����� �� �ʵ带 �ʱ�ȭ��
		/* �� ����� ���⼭ �ʱ�ȭ �� �� */
		this.name = name;		
		this.serviceTime = serviceTime;		
		executionTime = 0;
	}
	/* �Ʒ� �� �Լ��� ������ �� */
	/* ���μ����� ����ð��� ������Ŵ */
	public void incExecTime() { executionTime = executionTime + 1; }	 
	/* ���� ����ð��� ����� �� */
	public int getRemainingTime() { return (serviceTime - executionTime); } 	
	/* ���μ����� �̸��� ���� */
	public String getName() { return (name); }
	/* ���μ����� ������ ����Ǿ����� üũ��
	 * >> ���� ����� �ð��� �����ؾ� �� �� �ð��� ������ �����մϴ�. 
	 * >> �׳� ���⸸ �ϸ� �ȵǳ�?? : ���ܰ� �߻��ؼ� ���� ����� �ð����� �����ؾ� �� �� �ð��� �� Ŀ���� ��츦 ����߽��ϴ�.
	 * >> ���� ���α׷����� ==�� �۵��մϴ�! */	
	public boolean isFinished() { return (executionTime >= serviceTime); }
	
	/* ���ð��� ����� ��
	 * >> waiting time : ���� ����� �ð� - ���μ����� ������ �ð� */
	 public int getWaitingTime(int cTime) { return executionTime - arrivalTime; } 
}

// �ý��� ���� ���μ����� ���� ���� �����ϴ� Ŭ����
// ��� ����� static ����̸�, �� Ŭ������ ��ü�� �������� �ʴ´�.
// >> ������ ���μ��� ������ �����մϴ�.
class ProcessController {
	// ������ �� ���μ����� �̸�, �����ð�, ���񽺽ð� ���� �迭�� ������
	static private String processNames[] = { "A", "B", "C", "D", "E", "A", "B", "C", "D", "E" };
	static private int    arrivalTimes[] = {   0,   2,   4,   6,   8,  30,  32,  34,  36,  38 };
	static private int    serviceTimes[] = {   3,   6,   4,   5,   2,  6,   3,   4,   5,   2 };
	static private int	  index; // ���� ���� ������ ���μ����� �� �迭 �ε���

	static public void    reset()          { index = 0; } // �� �����ٷ��� �����ڿ��� �� �Լ��� ȣ����, ó������ �ٽ� �����ٸ��� �����ϰ��� �ϴ� ��� ȣ��
	static public boolean hasNextProcess() { return index < arrivalTimes.length; } // ���� �������� ���� ���μ����� �� �ִ��� ����

	// �� �ð��������� ȣ��Ǹ�, �� ���μ����� �����ð��� �ش� ���μ����� �����Ͽ� ������
	static public Process checkNewProcessArrival(int currentTime) {
		if ((index < arrivalTimes.length) && 			// ���� ������ ���μ���(index)�� ���� �ִ� ���
			(arrivalTimes[index] == currentTime)) {		// ����ð��� ���� ���μ���(index)�� �����ð��� ������ ���
			int i = index++;							// �ε��� ���� �������� �� ���� ���μ����� ����Ű�� ��			
			return new Process(processNames[i], arrivalTimes[i], serviceTimes[i]); // �ش� ���μ����� �����Ͽ� ��ȯ��
		}
		return null; // ���� ���� ���μ����� ������ �ð��� �ƴ� ��� null�� ������
	} 
	
}

// �����ٷ��� �⺻ Ʋ: �� �߻�Ŭ������ ��ӹ޾� �� �����ٸ� �˰����� �����ؾ� ��
// >> �ϴ� �� : �� �˰����� �θ� Ŭ������ SRT�� RR���� ����� �����ٸ� ������ �����մϴ�.
abstract class Scheduler {
	private String name;						// �����ٷ� �̸�
	protected int currentTime;					// ����ð�
	protected Process currentProcess; 			// ���� ����ǰ� �ִ� ���μ����� ���۷��� ����
	protected boolean isNewProcessArrived;		// ����ð��� ���ο� ���μ����� ������ ��� true, �ƴϸ� false 
	protected LinkedList<Process> readyQueue;	// ready ������ ��� ���μ������� ��� ���� ready queue
	
	Scheduler(String name) { // �����ٷ��� �� �ʵ带 �ʱ�ȭ��
		this.name = name;
		currentTime = -1;
		currentProcess = null;
		isNewProcessArrived = false;
		readyQueue = new LinkedList<Process>();
		ProcessController.reset(); // ���μ��� �����ڸ� �ʱ�ȭ��, ó������ �ٽ� �����ٸ��ϰ��� �� ��� �ݵ�� ȣ��
	}
	public void addReadyQueue(Process p) { // ready queue�� ���ο� ���μ��� �߰�
		readyQueue.add(p); // ť�� �� ���� ���μ��� p ����
	}
	public boolean isThereAnyProcessToExecute() { // ��� ���μ������� ������ ����Ǿ����� üũ
		// ���� ������ ���μ����� �ְų� �Ǵ� ���� ����Ǵ� ���μ����� �ְų�, �Ǵ� ready queue�� ��� ���� ���� ��� ���� ������ �ȵ� (true ��ȯ)
		// ���� ������ ���μ����� ���� ���� ����Ǵ� ���μ����� ���� ready queue�� ��� ������ �����ص� �� (false ��ȯ)
		return(ProcessController.hasNextProcess() || (currentProcess != null) || !readyQueue.isEmpty());
	}
	public String getName() { return name; } // �����ٷ� �̸� ��ȯ

	// ���� �����ٸ��Ͽ� ������ ������ �켱������ ���� ���� ���μ����� ������
	// �� �����ٸ� �˰��򺰷� �� �Լ��� �������Ͽ��� ��(�������̵�)
	// >> �ϴ��� : ������ �Ϸ�� ���μ����� ť���� �����մϴ�.
	public void schedule() {
		if ((currentProcess != null) && currentProcess.isFinished()) { // ���� �������� ���μ����� ������ �Ϸ�� ���			
			readyQueue.remove(currentProcess); // ���� ���μ���(currentProcess)�� ready queue���� ������					
			currentProcess = null;			
		}
		// �� �����ٸ� �˰��򺰷� �� �Լ��� �������ؾ� ��. ������ �Լ����� superŬ������ �� �Լ��� ���� ���� ȣ���ؾ� �ϸ�, 
		// �� �Լ����� ���ϵ� �� �켱������ ���� ���� ���μ����� �����ؾ� ��. SRTŬ������ schedule() �Լ� ����.
	}
	
	// �� �ð� �������� ȣ��Ǵ� �Լ�. �� �ð��������� clock interrupt�� ���� ����ȴٰ� �����ϸ� ��
	// �� �����ٸ� �˰��򺰷� �� �Լ��� �������ؾ� ��. ������ �Լ����� superŬ������ �� �Լ��� ���� ���� ȣ���ؾ� ��
	// >> �ϴ��� : ����ð��� ������Ű�� ���� ���μ����� �ִ��� Ȯ���մϴ�.
	public void clockInterrupt() {		
		currentTime++; // ����ð��� 1 ������		
		if (currentProcess != null) { // ���� ����ǰ� �ִ� ���μ����� �ִٸ�
			currentProcess.incExecTime(); // ���� ����Ǵ� ���μ����� ����ð��� 1 ������
			System.out.print(currentProcess.getName()); // ����Ǵ� ���μ����� �̸��� �����
		}
		else
			System.out.print(" "); // ���� ����Ǵ� ���μ����� ���� ��� ���

		Process p = ProcessController.checkNewProcessArrival(currentTime); // ���ο� ���μ����� �����ߴ��� üũ
		isNewProcessArrived = (p != null); // p�� ���� ������ ���μ��� ��ü
		if (isNewProcessArrived)
			addReadyQueue(p); // ���� ������ ���μ����� ready queue�� �� ���� �߰�
	}
}

// Shortest Remaining Next �˰����� ����
// ���� �ð��� ���� ª�� �ְ� �켱������ ���� ����
// >> ���ο� ���α׷��� �����ų� ������ �ִ� ���α׷��� ������ �����ٷ��� �����մϴ�.
// >> ���� �켱����(�����ð�)�� ���ٸ� ť�� ���� ������� �����մϴ�.
// >> SRT�� ������ �ؾ� �� �ð��� ���� ª�� ���� ���μ����� ���� ó���ϴ� ������ �˰����Դϴ�.
class SRT extends Scheduler { // �� �����ٸ� �˰����� Scheduler Ŭ������ ��� �޾Ƽ� �����ؾ� ��
	SRT(String name) { super(name); } //  Scheduler Ŭ������ ������ ȣ��

	// Scheduler Ŭ������ schedule()�� ������, ���⼭ ������ ������ �켱������ ���� ���� ���μ����� ������
	// >> ���� ���μ����� �����մϴ�.
	@Override
	public void schedule() { 
		super.schedule(); // ���� Ŭ������ Scheduler�� schedule()�� ���� ȣ���ؾ� ��, ���⼭ ���� ���μ����� ������ ����Ǿ����� ���� ���ŵ�
		Process nextProcess = readyQueue.peek(); // ready ť�� ��忡 �ִ� ���Ҹ� ��ȯ (���������� ����) , or return null
		// ready queue�� �ִ� ���μ����� �� remaining time�� ���� ���� ���μ���(�켱������ ���� ���� ���μ���)�� ã�´�.
		for (var p: readyQueue)  // ready queue�� ������ ���μ��� p�� ���� 
			if (p.getRemainingTime() < nextProcess.getRemainingTime())							
				nextProcess = p;	// �������  remaining time�� ���� ���� ���μ�����				
		currentProcess = nextProcess; // ���� ���õ� ���� �켱������ ���� ���μ�����. �� ���μ����� �����		
		// ���� �ý��ۿ����� ���⼭ currentProcess���� CPU�� �ѱ�.
	}

	// �� �ð� �������� ȣ��Ǵ� �Լ�. �� �ð��������� clock interrupt�� ���� ����ȴٰ� �����ϸ� ��
	// �� �����ٸ� �˰��򺰷� �� �Լ��� �������ؾ� ��. 
	// >> ���ο� ���μ����� �����߰ų� ���� ���μ����� ����Ǿ����� üũ�մϴ�.
	@Override
	public void clockInterrupt() {
		super.clockInterrupt(); // superŬ����(Scheduler)�� �� �Լ��� ���� ���� ȣ���ؾ� ��		
		// ���ͷ�Ʈ�� �� �������� ������ ����(���ο� ���μ��� ���� �Ǵ� �� �۾� ����)�� �����Ǹ� schedule() �Լ��� ȣ����
		// �� �˰����� ������(Preemptive) �˰�����
		if ( isNewProcessArrived ||  // ���ο� ���μ����� �����߰ų� �Ǵ�
		     ((currentProcess != null) && currentProcess.isFinished()) ) // ���� �������� �۾��� ����Ǿ��ų� 
			schedule(); // ���� �����ٸ��Ͽ� �켱������ ���� ���� ���μ����� �����Ͽ� �� ���μ����� �����Ŵ
	}
}

// Round Robin �˰����� ����
// >> ���μ����� �Ҵ�� �ð��� �� ����ϸ� ���� ���μ����� �����մϴ�.
// >> ���� ���μ����� ����ǰų�, ����ִٰ� �� ���μ����� �����ϰų�, time slice(time quantum) �پ��� �����ٷ��� ȣ���մϴ�.
// >> RR�� time quantum(ó���ð� ����)�� �������� ť�׵� ���μ����� ���������� ó���ϴ� ������ �˰����Դϴ�.
class RR extends Scheduler {
	private int quantum;    // RR �˰����� time quantum, time slice
	private int execTime;   // ���� ���μ����� ���ݲ� ����� �ð�, quantum ���� �۾ƾ� �ϸ� �����ϸ� time slice�� ��� ������ ����
							// �� ���� �� �ð� �������� 1�� ������ >> �۾��� clockInterrupt()���� ����˴ϴ�.
	
	RR(String name, int quantum) { 
		super(name);    // Scheduler Ŭ������ ������ ȣ��
		this.quantum = quantum; // RR �˰����� time quantum, time slice >> �Է�test quantum�� 1, 4�Դϴ�.
		execTime = 0;				
	}
	
	/*  �Ʒ� �� �Լ��� ������ �Ͽ��� ��(�����ε�)
	public void schedule(), public void clockInterrupt()
	
	ready queue ������ ���� ������ �Լ��� ����� �� ����
	boolean readyQueue.add(Process p); // ť�� �� ���� p ����, return true or throw IllegalStateException
	Process readyQueue.remove() // ť�� �� ���ʿ� �ִ� ���μ����� ������ �� �̸� ��ȯ, or throw NoSuchElementException
	*/
	
	// >> �ϴ� �� : ���� ���μ����� �����մϴ�.
	// >> ���� ���μ����� ������ �Ϸ�Ǿ��ٸ� execTime�� 0���� �ʱ�ȭ�մϴ�.
	@Override
	public void schedule() { 
		super.schedule(); // ���� Ŭ������ Scheduler�� schedule()�� ���� ȣ���ؾ� ��, ���⼭ ���� ���μ����� ������ ����Ǿ����� ���� ���ŵ�
		
		// >> �켱 ���� ���μ����� �����ߴ��� �˻��մϴ�
		// >> ���� ���μ����� ����Ǿ����� ����ť���� ���� ���μ����� �����ϰ� ����ð� 0���� �ʱ�ȭ�մϴ�.
		if(currentProcess == null) {
			// >> currentProcess = readyQueue.remove();
            // >> execTime = 0;
			// >> NoSuchElementException //////// ..???? try ~ catch�� �ذ�..!
	        try {
	        	// >> ������� readyQueue�� a b c�� ������ b c�� �������� �մϴ�.
	        	// >> �̸� �����ϱ� ���� �� ù��° ���Ҹ� �����߽��ϴ�.
	            currentProcess = readyQueue.remove();
	            execTime = 0 ;
	            } catch(NoSuchElementException e) {}
		}
		
		// >> ���μ����� ���� ������� �ʾ�����
		// >> ���μ����� �־��� ����ð��� �� ����ߴ��� �˻��մϴ�.
		// >> ���� ���μ����� ����ð��� �־��� ����ð��� ������ ���μ����� �����մϴ�.
		// >> ����ð��� �� ����� ���μ����� ť���� �����ϸ鼭 execTime�� 0���� �ʱ�ȭ�մϴ�. 
		// >> + �׷��� execTime == quantum���� ���ܹ߻����� execTime�� quantum���� Ŀ������ �ֱ� ������
		// >> �� ��츦 �����ؼ� ������ �������־����ϴ�. execTime >= time quantum
		else if(execTime >= quantum) 
		{  			
			// >> ������� readyQueue�� a b c�� ������ b c a�� �������� �մϴ�.
			// >> �̸� �����ϱ� ���� �� ù��° ���Ҹ� �� �������� �߰��ϰ� ù��° ���Ҹ� �����߽��ϴ�.
			readyQueue.add(currentProcess);			
			currentProcess = readyQueue.remove();
			execTime = 0;			
		}				
	}
	
	// �� �ð� �������� ȣ��Ǵ� �Լ�. �� �ð��������� clock interrupt�� ���� ����ȴٰ� �����ϸ� ��
	// �� �����ٸ� �˰��򺰷� �� �Լ��� �������ؾ� ��.
	// >> ����ð��� ������ŵ�ϴ�.
	// >> ���μ��� �����ߴ���, �����ߴ���, time quantum �� ����ߴ��� üũ�մϴ�.
	@Override	
	public void clockInterrupt() {
		super.clockInterrupt(); // superŬ����(Scheduler)�� �� �Լ��� ���� ���� ȣ���ؾ� ��        
		execTime = execTime + 1; // >> ���μ��� ����ð��� �ϳ��� ������ŵ�ϴ�.
		// >> System.out.println(" execTime : " + execTime); ����ð� Ȯ��
		
		// >> ���� �������� ���μ����� ������� �ʾҰ�
		// >> ���ο� ���μ����� �����߰ų� ���� ���μ����� �־��� ����ð��� �� ������� ���ߴٸ� �����ٷ��� ȣ���մϴ�.
		if ( isNewProcessArrived ||  // ���ο� ���μ����� �����߰ų� �Ǵ�
			     ((currentProcess != null) && currentProcess.isFinished())
			     || (execTime >= quantum)) // ���� �������� �۾��� ����Ǿ��ų� �־��� ����ð��� �� ������� ���ߴٸ�
			schedule(); // ���� �����ٸ��Ͽ� �켱������ ���� ���� ���μ����� �����Ͽ� �� ���μ����� �����Ŵ
	}
}

/* �ü�� ���� : ���α׷��� ���� ���� ����
 * SRT(Shortest Remaining Time) �����ٸ� �˰����� �����ؼ� RR(Round Robin) �����ٸ� �˰����� �����մϴ�.
 * �̸� : ����ȸ
 * �а� : ��ǻ�Ͱ��а� 
 * �й� : 20154215
 */
public class SchedulingApp_20154215 {

	public static void printEpilog(Scheduler scheduler) {
		/* ȭ�鿡 ������ ���� �����: �˰��� �̸� scheduler.getName()
		Scheduling Algorithm: �˰����̸�
		0         1         2         3         4         5    	// �ð� �ʴ���
		0123456789012345678901234567890123456789012345678901234 // �ð� �ϴ���
		*/
		/* �� ó�� ��µǰ� ���⿡ �ڵ��϶� */
		System.out.println("Scheduling Algorithm : " + scheduler.getName());
		System.out.println("0         1         2         3         4         5");
		System.out.println("0123456789012345678901234567890123456789012345678901234");		
	}

	public static void schedAppRun(Scheduler scheduler) { // �� �����ٸ� �˰����� �׽�Ʈ ��
		printEpilog(scheduler); // ȭ�鿡 �����ð� �����ڸ� �����

		while (scheduler.isThereAnyProcessToExecute()) { // ���� �� �����ؾ� �� ���μ����� �ִ��� üũ
			scheduler.clockInterrupt(); // �� �ð��������� �����ٷ��� clock interrupt handler�� ȣ����
			try {					// �� �ð������� 100ms
				Thread.sleep(100); 	// 100 millisecond ���� ������, �� 100ms���� �ѹ��� �� scheduler.clockInterrupt()�� ȣ���
			}
			catch (InterruptedException e) { // sleep()�ϴ� ���� �ٸ� �����忡 ���� ���ͷ��� ��� �� ���, ���⼭�� ���� �߻����� ���� 
				e.printStackTrace(); // InterruptedException�� �߻����� ��� ���ݲ� ȣ��� �Լ� ����Ʈ�� ����� �� �� ����
				return;
			}
		}
		System.out.println("\n");
	}
	
	public static void main(String[] args) {
		schedAppRun(new SRT("SRT")); // SRT �����ٷ� ��ü�� ������ �� �����ٸ� �˰����� �׽�Ʈ ��
		/* �Ʒ� // �ּ��� ������ �� ����϶�. */
		schedAppRun(new RR("RR q=1", 1)); // >> RR �����ٷ� ��ü�� ������ �� �����ٸ� �˰����� �׽�Ʈ �մϴ�.
		schedAppRun(new RR("RR q=4", 4)); // >> RR �����ٷ� ��ü�� ������ �� �����ٸ� �˰����� �׽�Ʈ �մϴ�.			
	}
}

/*  AAABCCCCEEBBBBBDDDDD          AABBBAAAAEECCCCDDDDD
 *  AAABCCCCEEBBBBBDDDDD          AABBBAAAAEECCCCDDDDD
 * 
 *  AABABCBDCBEDCBEDCBDD          AABABCABDCAEDCAEDCDD
 *  AABABCBDCBEDCBEDCBDD          AABABCABDCAEDCAEDCDD
 * 
 *  AAABBBBCCCCDDDDBBEED          AAAABBBCCCCAADDDDEED
 *  AAABBBBCCCCDDDDBBEED          AAAABBBCCCCAADDDDEED
 */
