package unl.cse;

import java.util.Iterator;

public class MyList<T> implements Iterable <T>{
	private int capacity;
	private T myArray[];
	private int size;
	
	public MyList() {
		this.myArray = (T[]) new Object[64];
		this.capacity = 64;
		this.size = 0;
	}
	
	private MyList(int capacity){
		this.myArray = (T[]) new Object[capacity];
		this.capacity = capacity;
		this.size = 0;
	}

	//Check
	public T get(int index) {
		if(index < 0 || index >= size) 
			//TODO: LOG ERROR
			throw new IllegalArgumentException("index = " + index + " is out of bounds");
		else
			return this.myArray[index];
	}
	
	//Check
	public void remove(int index) {

		if(index < 0 || index >= size) 
			//TODO: LOG ERROR
			throw new IllegalArgumentException("index = "+index+" is out of bounds");
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

		if(size == capacity) {
			//resize
			T tmp[] = (T[]) new Object[capacity * 2];
			capacity *= 2;
			for(int i=0; i<myArray.length; i++) {
				tmp[i] = myArray[i];
			}
			this.myArray = tmp;
		}

		this.myArray[size] = element;
		this.size++;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public boolean isEmpty() {
		return (size == 0) ? true : false;
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
