package playground.mmoyo.pttest;

import org.matsim.basic.v01.IdImpl;
import org.matsim.network.Node;

public class PTNode extends Node {
	private IdImpl idFather;
	private IdImpl IdPTLine;

	public PTNode(IdImpl idImpl, final String x, final String y, final String type, IdImpl idFather, IdImpl IdPtLine) {
		super(idImpl, x, y, type);
		this.idFather = idFather;
		this.IdPTLine = IdPtLine;
	}

	public PTNode(IdImpl idImpl, final String x, final String y, final String type) {
		super(idImpl, x, y, type);
	}

	public IdImpl getIdFather() {
		return idFather;
	}

	public void setIdFather(IdImpl idFather) {
		this.idFather = idFather;
	}

	public IdImpl getIdPTLine() {
		return IdPTLine;
	}
}
