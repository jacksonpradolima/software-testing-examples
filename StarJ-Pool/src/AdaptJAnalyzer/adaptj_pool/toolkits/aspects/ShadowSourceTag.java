package adaptj_pool.toolkits.aspects;

public class ShadowSourceTag {
	private int shadowId;
	private int sourceId;
	private long instrCount;
	private long appInstrCount;
	private long adviceInvokeCount;
	private long appAdviceInvokeCount;
	
	public ShadowSourceTag(int shadowId, int sourceId) {
		this.shadowId = shadowId;
		this.sourceId = sourceId;
	}

	public int getShadowId() {
		return shadowId;
	}
	
	public int getSourceId() {
		return sourceId;
	}
	
	public long incrInstrCount() {
		return ++this.instrCount;
	}
	
	public long getInstrCount() {
		return instrCount;
	}

	public long incrAppInstrCount() {
		return ++this.appInstrCount;
	}
	
	public long incrAdviceInvokeCount() {
		return ++this.adviceInvokeCount;
	}
	
	public long getAdviceInvokeCount() {
		return this.adviceInvokeCount;
	}

	public long incrAppAdviceInvokeCount() {
		return ++this.appAdviceInvokeCount;
	}
	
	public long getAppAdviceInvokeCount() {
		return this.appAdviceInvokeCount;
	}
}
