package com.whiteout.task;

import java.util.Comparator;

public class ToDoComparator implements Comparator<ToDoItem>{

	@Override
	public int compare(ToDoItem arg0, ToDoItem arg1) {
		return arg0.getPriority().compareTo(arg1.getPriority());
	}

}
