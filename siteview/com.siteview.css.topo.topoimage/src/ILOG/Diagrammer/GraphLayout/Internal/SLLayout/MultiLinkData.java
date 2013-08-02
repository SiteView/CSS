package ILOG.Diagrammer.GraphLayout.Internal.SLLayout;

import system.*;
import system.Collections.*;

public final class MultiLinkData extends LinkData {
	private ArrayList _individualLinks;

	private float _linkOffset;

	private float _linkOffsetDest;

	private float _linkOffsetOrig;

	public MultiLinkData(ShortLinkAlgorithm layout, LinkData firstSlaveLink,
			float linkOffset) {
		super(layout, firstSlaveLink.get_nodeOrLink(), firstSlaveLink);
		this.AddSlave(firstSlaveLink);
	}

	public void AddSlave(LinkData slaveLinkData) {
		if (this._individualLinks == null) {
			this._individualLinks = new ArrayList(10);
		}
		slaveLinkData.SetSlave(true);
		this._individualLinks.Add(slaveLinkData);
		if (slaveLinkData.get_nodeOrLink() != super.get_nodeOrLink()) {
			super._linkWidth += slaveLinkData.GetLinkWidth();
		}
		slaveLinkData.SetMasterLink(this);

	}

	@Override
	public void CleanMasterSlaveInfo(float linkOffset, LinkData firstSlaveLink) {
		super.CleanMasterSlaveInfo(linkOffset, firstSlaveLink);
		if (this._individualLinks != null) {
			this._individualLinks.Clear();
		}
		this.SetLinkOffset(linkOffset, true, true);
		super.CopyIncrementalData(firstSlaveLink);

	}

	public float GetDestinationLinkOffset() {

		return this._linkOffsetDest;

	}

	public ArrayList GetIndividualLinks() {

		return this._individualLinks;

	}

	@Override
	public float GetLinkWidth() {

		return this.GetLinkWidth(this._linkOffset);

	}

	@Override
	public float GetLinkWidth(float linkOffset) {
		if (linkOffset == 0f) {

			return super._linkWidth;
		}
		Integer numberOfIndividualLinks = this.GetNumberOfIndividualLinks();
		if (numberOfIndividualLinks < 2) {

			return super._linkWidth;
		}

		return (super._linkWidth + ((numberOfIndividualLinks - 1) * linkOffset));

	}

	@Override
	public Integer GetNumberOfIndividualLinks() {
		if (this._individualLinks != null) {

			return this._individualLinks.get_Count();
		}

		return 0;

	}

	public float GetOriginLinkOffset() {

		return this._linkOffsetOrig;

	}

	@Override
	public Boolean IsMaster() {

		return true;

	}

	@Override
	public void SetDestinationLinkOffset(float offset) {
		this._linkOffsetDest = offset;

	}

	public void SetLinkOffset(float linkOffset, Boolean copyToOriginOffset,
			Boolean copyToDestinationOffset) {
		this._linkOffset = linkOffset;
		if (copyToOriginOffset) {
			this._linkOffsetOrig = linkOffset;
		}
		if (copyToDestinationOffset) {
			this._linkOffsetDest = linkOffset;
		}

	}

	@Override
	public void SetOriginLinkOffset(float offset) {
		this._linkOffsetOrig = offset;

	}

}