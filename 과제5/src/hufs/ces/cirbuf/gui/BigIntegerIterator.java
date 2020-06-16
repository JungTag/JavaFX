package hufs.ces.cirbuf.gui;

import java.math.BigInteger;
import java.util.Iterator;

public class BigIntegerIterator implements Iterator<BigInteger> {
	
	private BigInteger bound = new BigInteger("-1");
	private BigInteger curValue = BigInteger.ZERO;;
	private BigInteger stepValue = BigInteger.ONE;

	public BigIntegerIterator(){}
	public BigIntegerIterator(BigInteger bound){
		this.bound = bound;
	}
	public BigIntegerIterator(BigInteger start, BigInteger bound){
		this.curValue = start;
		this.bound = bound;
	}
	public BigIntegerIterator(BigInteger start, BigInteger bound, BigInteger step){
		this(start, bound);
		this.stepValue = step;
	}
	public boolean hasNext() {
		if (bound.equals(new BigInteger("-1")))
			return true;
		
		if (curValue.compareTo(bound) < 0)
			return true;
		else
			return false;
	}

	public BigInteger next() {
		BigInteger retValue = curValue;
		curValue = curValue.add(stepValue);
		return retValue;
	}

	public void remove() {
		throw new UnsupportedOperationException();

	}

}
