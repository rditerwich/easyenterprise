package easyenterprise.lib.sexpr;

public abstract class AbstractFunction implements SExprFunction {
	
	private final String name;
	private final int minPar;
	private final int maxPar;

	public AbstractFunction(String name, int minPar, int maxPar) {
		this.name = name;
		this.minPar = minPar;
		this.maxPar = maxPar;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMinParameters() {
		return minPar;
	}

	@Override
	public int getMaxParameters() {
		return maxPar;
	}
}
