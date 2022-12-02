package com.scrip.study;

import java.util.Iterator;

public class Sequence implements Iterable<Integer>{

    public static void main(String[] args) {
        Sequence s = new Sequence();
        Iterator<Integer> iterator = s.iterator();
        while (iterator.hasNext()) {
            System.out.println("iterator.next() = " + iterator.next());
        }
    }
    
    private int value;

    @Override
    public Iterator<Integer> iterator() {
        return new SequenceIterator(this);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    class SequenceIterator implements Iterator<Integer> {
        Sequence sequence;
        
        public SequenceIterator(Sequence sequence) {
            this.sequence = sequence;
        }

        @Override
        public boolean hasNext() {
            return sequence.getValue()<100;
        }

        @Override
        public Integer next() {
            sequence.setValue(sequence.getValue()+1);
            return sequence.getValue();
        }
    }
}
