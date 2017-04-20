package unl.cse;

import java.util.Comparator;
import java.util.Iterator;

public class MyList<T> implements Iterable <T>{
	private int capacity;
	private T myArray[];
	private int size;
	private Comparator<T> c = null;
	
	public MyList() {
		this.myArray = (T[]) new Object[64];
		this.capacity = 64;
		this.size = 0;
	}
	
	public MyList(Comparator c) {
		this.myArray = (T[]) new Object[64];
		this.capacity = 64;
		this.size = 0;
		this.c = c;
	}
	
	private MyList(int capacity){
		this.myArray = (T[]) new Object[capacity];
		this.capacity = capacity;
		this.size = 0;
	}

	//Check
	public T get(int index) {
		if(index < 0 || index >= size) {
			Driver.logger.warning("Get failed: Illegal index");
			return null;
		} else {
			return this.myArray[index];
		}
	}
	
	//Check
	public void remove(int index) {

		if(index < 0 || index >= size) {
			Driver.logger.warning("Remove failed: Illegal index");
			return;
		}
		
		for(int i=index; i<size-1; i++) {
			this.myArray[i] = this.myArray[i+1];
		}
		this.size--;
		
		if(size < capacity/2) {
			//resize
			T tmp[] = (T[]) new Object[capacity/2];
			capacity /= 2;
			for(int i=0; i<size; i++) {
				tmp[i] = this.myArray[i];
			}
			this.myArray = tmp;
		}
		
	}
	
	public void add(T element) {

		if (this.size == 0){
			myArray[0] = element;
			this.size++;
			return;
		}
		
		if(size == capacity) {
			//resize
			T tmp[] = (T[]) new Object[capacity * 2];
			capacity *= 2;
			
			for(int i=0; i<myArray.length; i++) {
				tmp[i] = myArray[i];
			}
			
			this.myArray = tmp;
		}
		
		boolean flag = false;
				
		if (c != null) {
			
			for (int i = 0 ; i < this.size ; i++){
				if (c.compare(element, myArray[i]) <= 0){
					flag = true;
					
					for (int j = this.size ; j > i ; j--){
						myArray[j] = myArray[j - 1];
					}
					
					myArray[i] = element;
					break;
				} 
			}
		}
		
		if (flag == false){
			this.myArray[size] = element;
		}
		
		this.size++;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public boolean isEmpty() {
		if(size == 0){
			return true;
		}
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		Iterator<T> it = new Iterator<T>(){

			private int currentIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentIndex < size && (T)myArray[currentIndex] != null;
			}

			@Override
			public T next() {
				return (T)myArray[currentIndex++];
			}
			
			public void remove(){
				return;
			}
			
		};
		return it;
	}
}
