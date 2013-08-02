package ILOG.Diagrammer.GraphLayout.Internal.LLLayout;

import system.*;

public final class LLCrossPoint {
	private Integer[] _crossIndex = new Integer[2];

	private Integer _crossSegIndexA;

	private Integer _crossSegIndexB;

	private Integer _distFromStartA;

	private Integer _distFromStartB;

	private Integer _hCrossSegMax;

	private Integer _hCrossSegMin;

	private LLCrossPoint _next = null;

	private Integer _type;

	private Integer _vCrossSegMax;

	private Integer _vCrossSegMin;

	public LLCrossPoint() {
	}

	public void CalcBypassPoint(Integer[] point) {
		if ((this._type % 4) == 0) {
			point[0] = this._hCrossSegMax;
			point[1] = this._vCrossSegMin;

			return;
		} else if ((this._type % 4) == 1) {
			point[0] = this._hCrossSegMin;
			point[1] = this._vCrossSegMin;

			return;
		} else if ((this._type % 4) == 2) {
			point[0] = this._hCrossSegMax;
			point[1] = this._vCrossSegMax;

			return;
		} else if ((this._type % 4) == 3) {
			point[0] = this._hCrossSegMin;
			point[1] = this._vCrossSegMax;

			return;
		}

	}

	public Boolean CheckCrossing(Integer segA1A2Index, Integer distToA1,
			Integer[] pA1, Integer[] pA2, Integer[] pA3, Integer segB1B2Index,
			Integer distToB1, Integer[] pB1, Integer[] pB2, Integer[] pB3) {
		this._type = -1;
		if (pA1[0] == pA2[0]) {
			if (pB1[0] == pB2[0]) {
				if ((pA1[1] < pB3[1]) && (pB3[1] < pA2[1])) {
					this._crossIndex[0] = pA1[0];
					this._crossIndex[1] = pB3[1];
					this._crossSegIndexA = segA1A2Index;
					this._crossSegIndexB = segB1B2Index + 1;
					if ((pB2[0] < pA1[0]) && (pA1[0] < pB3[0])) {
						if ((pB1[1] <= pB2[1]) && (pA2[0] <= pA3[0])) {
							this._type = 3;
							this._hCrossSegMin = pB2[0];
							this._hCrossSegMax = pB3[0];
							this._vCrossSegMin = pA1[1];
							this._vCrossSegMax = pA2[1];
							this._distFromStartA = (distToA1 + this._crossIndex[1])
									- this._vCrossSegMin;
							this._distFromStartB = (((distToB1 + pA3[0]) - pA2[0]) + this._crossIndex[0])
									- this._hCrossSegMin;
						}
					} else if (((pB2[0] > pA1[0]) && (pA1[0] > pB3[0]))
							&& ((pB1[1] <= pB2[1]) && (pA3[0] <= pA2[0]))) {
						this._type = 6;
						this._hCrossSegMin = pB3[0];
						this._hCrossSegMax = pB2[0];
						this._vCrossSegMin = pA1[1];
						this._vCrossSegMax = pA2[1];
						this._distFromStartA = (distToA1 + this._crossIndex[1])
								- this._vCrossSegMin;
						this._distFromStartB = (((distToB1 + pB2[1]) - pB1[1]) + this._hCrossSegMax)
								- this._crossIndex[0];
					}
				} else if ((pA1[1] > pB3[1]) && (pB3[1] > pA2[1])) {
					this._crossIndex[0] = pA1[0];
					this._crossIndex[1] = pB3[1];
					this._crossSegIndexA = segA1A2Index;
					this._crossSegIndexB = segB1B2Index + 1;
					if ((pB2[0] < pA1[0]) && (pA1[0] < pB3[0])) {
						if ((pB2[1] <= pB1[1]) && (pA2[0] <= pA3[0])) {
							this._type = 1;
							this._hCrossSegMin = pB2[0];
							this._hCrossSegMax = pB3[0];
							this._vCrossSegMin = pA2[1];
							this._vCrossSegMax = pA1[1];
							this._distFromStartA = (distToA1 + this._vCrossSegMax)
									- this._crossIndex[1];
							this._distFromStartB = (((distToB1 + pB1[1]) - pB2[1]) + this._crossIndex[0])
									- this._hCrossSegMin;
						}
					} else if (((pB2[0] > pA1[0]) && (pA1[0] > pB3[0]))
							&& ((pB2[1] <= pB1[1]) && (pA3[0] <= pA2[0]))) {
						this._type = 4;
						this._hCrossSegMin = pB3[0];
						this._hCrossSegMax = pB2[0];
						this._vCrossSegMin = pA2[1];
						this._vCrossSegMax = pA1[1];
						this._distFromStartA = (distToA1 + this._vCrossSegMax)
								- this._crossIndex[1];
						this._distFromStartB = (((distToB1 + pB1[1]) - pB2[1]) + this._hCrossSegMax)
								- this._crossIndex[0];
					}
				} else if ((pB1[1] < pA3[1]) && (pA3[1] < pB2[1])) {
					this._crossIndex[0] = pB1[0];
					this._crossIndex[1] = pA3[1];
					this._crossSegIndexA = segA1A2Index + 1;
					this._crossSegIndexB = segB1B2Index;
					if ((pA2[0] < pB1[0]) && (pB1[0] < pA3[0])) {
						if ((pA1[1] <= pA2[1]) && (pB2[0] <= pB3[0])) {
							this._type = 3;
							this._hCrossSegMin = pA2[0];
							this._hCrossSegMax = pA3[0];
							this._vCrossSegMin = pB1[1];
							this._vCrossSegMax = pB2[1];
							this._distFromStartA = (((distToA1 + pA2[1]) - pA1[1]) + this._crossIndex[0])
									- this._hCrossSegMin;
							this._distFromStartB = (distToB1 + this._crossIndex[1])
									- this._vCrossSegMin;
						}
					} else if (((pA2[0] > pB1[0]) && (pB1[0] > pA3[0]))
							&& ((pA1[1] <= pA2[1]) && (pB3[0] <= pB2[0]))) {
						this._type = 6;
						this._hCrossSegMin = pA3[0];
						this._hCrossSegMax = pA2[0];
						this._vCrossSegMin = pB1[1];
						this._vCrossSegMax = pB2[1];
						this._distFromStartA = (((distToA1 + pA2[1]) - pA1[1]) + this._hCrossSegMax)
								- this._crossIndex[0];
						this._distFromStartB = (distToB1 + this._crossIndex[1])
								- this._vCrossSegMin;
					}
				} else if ((pB1[1] > pA3[1]) && (pA3[1] > pB2[1])) {
					this._crossIndex[0] = pB1[0];
					this._crossIndex[1] = pA3[1];
					this._crossSegIndexA = segA1A2Index + 1;
					this._crossSegIndexB = segB1B2Index;
					if ((pA2[0] < pB1[0]) && (pB1[0] < pA3[0])) {
						if ((pA2[1] <= pA1[1]) && (pB2[0] <= pB3[0])) {
							this._type = 1;
							this._hCrossSegMin = pA2[0];
							this._hCrossSegMax = pA3[0];
							this._vCrossSegMin = pB2[1];
							this._vCrossSegMax = pB1[1];
							this._distFromStartA = (((distToA1 + pA1[1]) - pA2[1]) + this._crossIndex[0])
									- this._hCrossSegMin;
							this._distFromStartB = (distToB1 + this._vCrossSegMax)
									- this._crossIndex[1];
						}
					} else if (((pA2[0] > pB1[0]) && (pB1[0] > pA3[0]))
							&& ((pA2[1] <= pA1[1]) && (pB3[0] <= pB2[0]))) {
						this._type = 4;
						this._hCrossSegMin = pA3[0];
						this._hCrossSegMax = pA2[0];
						this._vCrossSegMin = pB2[1];
						this._vCrossSegMax = pB1[1];
						this._distFromStartA = (((distToA1 + pA1[1]) - pA2[1]) + this._hCrossSegMax)
								- this._crossIndex[0];
						this._distFromStartB = (distToB1 + this._vCrossSegMax)
								- this._crossIndex[1];
					}
				}
			} else if ((pA1[1] < pB1[1]) && (pB1[1] < pA2[1])) {
				this._crossIndex[0] = pA1[0];
				this._crossIndex[1] = pB1[1];
				this._crossSegIndexA = segA1A2Index;
				this._crossSegIndexB = segB1B2Index;
				if ((pB1[0] < pA1[0]) && (pA1[0] < pB2[0])) {
					if ((pB3[1] <= pB2[1]) && (pA3[0] <= pA2[0])) {
						this._type = 10;
						this._hCrossSegMin = pB1[0];
						this._hCrossSegMax = pB2[0];
						this._vCrossSegMin = pA1[1];
						this._vCrossSegMax = pA2[1];
						this._distFromStartA = (distToA1 + this._crossIndex[1])
								- this._vCrossSegMin;
						this._distFromStartB = (distToB1 + this._crossIndex[0])
								- this._hCrossSegMin;
					}
				} else if (((pB1[0] > pA1[0]) && (pA1[0] > pB2[0]))
						&& ((pB3[1] <= pB2[1]) && (pA2[0] <= pA3[0]))) {
					this._type = 15;
					this._hCrossSegMin = pB2[0];
					this._hCrossSegMax = pB1[0];
					this._vCrossSegMin = pA1[1];
					this._vCrossSegMax = pA2[1];
					this._distFromStartA = (distToA1 + this._crossIndex[1])
							- this._vCrossSegMin;
					this._distFromStartB = (distToB1 + this._hCrossSegMax)
							- this._crossIndex[0];
				}
			} else if ((pA1[1] > pB1[1]) && (pB1[1] > pA2[1])) {
				this._crossIndex[0] = pA1[0];
				this._crossIndex[1] = pB1[1];
				this._crossSegIndexA = segA1A2Index;
				this._crossSegIndexB = segB1B2Index;
				if ((pB1[0] < pA1[0]) && (pA1[0] < pB2[0])) {
					if ((pB2[1] <= pB3[1]) && (pA3[0] <= pA2[0])) {
						this._type = 12;
						this._hCrossSegMin = pB1[0];
						this._hCrossSegMax = pB2[0];
						this._vCrossSegMin = pA2[1];
						this._vCrossSegMax = pA1[1];
						this._distFromStartA = (distToA1 + this._vCrossSegMax)
								- this._crossIndex[1];
						this._distFromStartB = (distToB1 + this._crossIndex[0])
								- this._hCrossSegMin;
					}
				} else if (((pB1[0] > pA1[0]) && (pA1[0] > pB2[0]))
						&& ((pB2[1] <= pB3[1]) && (pA2[0] <= pA3[0]))) {
					this._type = 9;
					this._hCrossSegMin = pB2[0];
					this._hCrossSegMax = pB1[0];
					this._vCrossSegMin = pA2[1];
					this._vCrossSegMax = pA1[1];
					this._distFromStartA = (distToA1 + this._vCrossSegMax)
							- this._crossIndex[1];
					this._distFromStartB = (distToB1 + this._hCrossSegMax)
							- this._crossIndex[0];
				}
			} else if ((pB2[1] < pA3[1]) && (pA3[1] < pB3[1])) {
				this._crossIndex[0] = pB3[0];
				this._crossIndex[1] = pA3[1];
				this._crossSegIndexA = segA1A2Index + 1;
				this._crossSegIndexB = segB1B2Index + 1;
				if ((pA2[0] < pB3[0]) && (pB3[0] < pA3[0])) {
					if ((pA2[1] <= pA1[1]) && (pB2[0] <= pB1[0])) {
						this._type = 13;
						this._hCrossSegMin = pA2[0];
						this._hCrossSegMax = pA3[0];
						this._vCrossSegMin = pB2[1];
						this._vCrossSegMax = pB3[1];
						this._distFromStartA = (((distToA1 + pA1[1]) - pA2[1]) + this._crossIndex[0])
								- this._hCrossSegMin;
						this._distFromStartB = (((distToB1 + pB1[0]) - pB2[0]) + this._crossIndex[1])
								- this._vCrossSegMin;
					}
				} else if (((pA2[0] > pB3[0]) && (pB3[0] > pA3[0]))
						&& ((pA2[1] <= pA1[1]) && (pB1[0] <= pB2[0]))) {
					this._type = 8;
					this._hCrossSegMin = pA3[0];
					this._hCrossSegMax = pA2[0];
					this._vCrossSegMin = pB2[1];
					this._vCrossSegMax = pB3[1];
					this._distFromStartA = (((distToA1 + pA1[1]) - pA2[1]) + this._hCrossSegMax)
							- this._crossIndex[0];
					this._distFromStartB = (((distToB1 + pB2[0]) - pB1[0]) + this._crossIndex[1])
							- this._vCrossSegMin;
				}
			} else if ((pB2[1] > pA3[1]) && (pA3[1] > pB3[1])) {
				this._crossIndex[0] = pB3[0];
				this._crossIndex[1] = pA3[1];
				this._crossSegIndexA = segA1A2Index + 1;
				this._crossSegIndexB = segB1B2Index + 1;
				if ((pA2[0] < pB3[0]) && (pB3[0] < pA3[0])) {
					if ((pA1[1] <= pA2[1]) && (pB2[0] <= pB1[0])) {
						this._type = 11;
						this._hCrossSegMin = pA2[0];
						this._hCrossSegMax = pA3[0];
						this._vCrossSegMin = pB3[1];
						this._vCrossSegMax = pB2[1];
						this._distFromStartA = (((distToA1 + pA2[1]) - pA1[1]) + this._crossIndex[0])
								- this._hCrossSegMin;
						this._distFromStartB = (((distToB1 + pB1[0]) - pB2[0]) + this._vCrossSegMax)
								- this._crossIndex[1];
					}
				} else if (((pA2[0] > pB3[0]) && (pB3[0] > pA3[0]))
						&& ((pA1[1] <= pA2[1]) && (pB1[0] <= pB2[0]))) {
					this._type = 14;
					this._hCrossSegMin = pA3[0];
					this._hCrossSegMax = pA2[0];
					this._vCrossSegMin = pB3[1];
					this._vCrossSegMax = pB2[1];
					this._distFromStartA = (((distToA1 + pA2[1]) - pA1[1]) + this._hCrossSegMax)
							- this._crossIndex[0];
					this._distFromStartB = (((distToB1 + pB2[0]) - pB1[0]) + this._vCrossSegMax)
							- this._crossIndex[1];
				}
			}
		} else if (pB1[0] == pB2[0]) {
			if ((pA2[1] < pB3[1]) && (pB3[1] < pA3[1])) {
				this._crossIndex[0] = pA3[0];
				this._crossIndex[1] = pB3[1];
				this._crossSegIndexA = segA1A2Index + 1;
				this._crossSegIndexB = segB1B2Index + 1;
				if ((pB2[0] < pA3[0]) && (pA3[0] < pB3[0])) {
					if ((pB2[1] <= pB1[1]) && (pA2[0] <= pA1[0])) {
						this._type = 13;
						this._hCrossSegMin = pB2[0];
						this._hCrossSegMax = pB3[0];
						this._vCrossSegMin = pA2[1];
						this._vCrossSegMax = pA3[1];
						this._distFromStartA = (((distToA1 + pA1[0]) - pA2[0]) + this._crossIndex[1])
								- this._vCrossSegMin;
						this._distFromStartB = (((distToB1 + pB1[1]) - pB2[1]) + this._crossIndex[0])
								- this._hCrossSegMin;
					}
				} else if (((pB2[0] > pA3[0]) && (pA3[0] > pB3[0]))
						&& ((pB2[1] <= pB1[1]) && (pA1[0] <= pA2[0]))) {
					this._type = 8;
					this._hCrossSegMin = pB3[0];
					this._hCrossSegMax = pB2[0];
					this._vCrossSegMin = pA2[1];
					this._vCrossSegMax = pA3[1];
					this._distFromStartA = (((distToA1 + pA2[0]) - pA1[0]) + this._crossIndex[1])
							- this._vCrossSegMin;
					this._distFromStartB = (((distToB1 + pB1[1]) - pB2[1]) + this._hCrossSegMax)
							- this._crossIndex[0];
				}
			} else if ((pA2[1] > pB3[1]) && (pB3[1] > pA3[1])) {
				this._crossIndex[0] = pA3[0];
				this._crossIndex[1] = pB3[1];
				this._crossSegIndexA = segA1A2Index + 1;
				this._crossSegIndexB = segB1B2Index + 1;
				if ((pB2[0] < pA3[0]) && (pA3[0] < pB3[0])) {
					if ((pB1[1] <= pB2[1]) && (pA2[0] <= pA1[0])) {
						this._type = 11;
						this._hCrossSegMin = pB2[0];
						this._hCrossSegMax = pB3[0];
						this._vCrossSegMin = pA3[1];
						this._vCrossSegMax = pA2[1];
						this._distFromStartA = (((distToA1 + pA1[0]) - pA2[0]) + this._vCrossSegMax)
								- this._crossIndex[1];
						this._distFromStartB = (((distToB1 + pB2[1]) - pB1[1]) + this._crossIndex[0])
								- this._hCrossSegMin;
					}
				} else if (((pB2[0] > pA3[0]) && (pA3[0] > pB3[0]))
						&& ((pB1[1] <= pB2[1]) && (pA1[0] <= pA2[0]))) {
					this._type = 14;
					this._hCrossSegMin = pB3[0];
					this._hCrossSegMax = pB2[0];
					this._vCrossSegMin = pA3[1];
					this._vCrossSegMax = pA2[1];
					this._distFromStartA = (((distToA1 + pA2[0]) - pA1[0]) + this._vCrossSegMax)
							- this._crossIndex[1];
					this._distFromStartB = (((distToB1 + pB2[1]) - pB1[1]) + this._hCrossSegMax)
							- this._crossIndex[0];
				}
			} else if ((pB1[1] < pA2[1]) && (pA2[1] < pB2[1])) {
				this._crossIndex[0] = pB1[0];
				this._crossIndex[1] = pA1[1];
				this._crossSegIndexA = segA1A2Index;
				this._crossSegIndexB = segB1B2Index;
				if ((pA1[0] < pB1[0]) && (pB1[0] < pA2[0])) {
					if ((pA3[1] <= pA2[1]) && (pB3[0] <= pB2[0])) {
						this._type = 10;
						this._hCrossSegMin = pA1[0];
						this._hCrossSegMax = pA2[0];
						this._vCrossSegMin = pB1[1];
						this._vCrossSegMax = pB2[1];
						this._distFromStartA = (distToA1 + this._crossIndex[0])
								- this._hCrossSegMin;
						this._distFromStartB = (distToB1 + this._crossIndex[1])
								- this._vCrossSegMin;
					}
				} else if (((pA1[0] > pB1[0]) && (pB1[0] > pA2[0]))
						&& ((pA3[1] <= pA2[1]) && (pB2[0] <= pB3[0]))) {
					this._type = 15;
					this._hCrossSegMin = pA2[0];
					this._hCrossSegMax = pA1[0];
					this._vCrossSegMin = pB1[1];
					this._vCrossSegMax = pB2[1];
					this._distFromStartA = (distToA1 + this._hCrossSegMax)
							- this._crossIndex[0];
					this._distFromStartB = (distToB1 + this._crossIndex[1])
							- this._vCrossSegMin;
				}
			} else if ((pB1[1] > pA2[1]) && (pA2[1] > pB2[1])) {
				this._crossIndex[0] = pB1[0];
				this._crossIndex[1] = pA1[1];
				this._crossSegIndexA = segA1A2Index;
				this._crossSegIndexB = segB1B2Index;
				if ((pA1[0] < pB1[0]) && (pB1[0] < pA2[0])) {
					if ((pA2[1] <= pA3[1]) && (pB3[0] <= pB2[0])) {
						this._type = 12;
						this._hCrossSegMin = pA1[0];
						this._hCrossSegMax = pA2[0];
						this._vCrossSegMin = pB2[1];
						this._vCrossSegMax = pB1[1];
						this._distFromStartA = (distToA1 + this._crossIndex[0])
								- this._hCrossSegMin;
						this._distFromStartB = (distToB1 + this._vCrossSegMax)
								- this._crossIndex[1];
					}
				} else if (((pA1[0] > pB1[0]) && (pB1[0] > pA2[0]))
						&& ((pA2[1] <= pA3[1]) && (pB2[0] <= pB3[0]))) {
					this._type = 9;
					this._hCrossSegMin = pA2[0];
					this._hCrossSegMax = pA1[0];
					this._vCrossSegMin = pB2[1];
					this._vCrossSegMax = pB1[1];
					this._distFromStartA = (distToA1 + this._hCrossSegMax)
							- this._crossIndex[0];
					this._distFromStartB = (distToB1 + this._vCrossSegMax)
							- this._crossIndex[1];
				}
			}
		} else if ((pA2[1] < pB1[1]) && (pB1[1] < pA3[1])) {
			this._crossIndex[0] = pA3[0];
			this._crossIndex[1] = pB1[1];
			this._crossSegIndexA = segA1A2Index + 1;
			this._crossSegIndexB = segB1B2Index;
			if ((pB1[0] < pA3[0]) && (pA3[0] < pB2[0])) {
				if ((pB2[1] <= pB3[1]) && (pA1[0] <= pA2[0])) {
					this._type = 0;
					this._hCrossSegMin = pB1[0];
					this._hCrossSegMax = pB2[0];
					this._vCrossSegMin = pA2[1];
					this._vCrossSegMax = pA3[1];
					this._distFromStartA = (((distToA1 + pA2[0]) - pA1[0]) + this._crossIndex[1])
							- this._vCrossSegMin;
					this._distFromStartB = (distToB1 + this._crossIndex[0])
							- this._hCrossSegMin;
				}
			} else if (((pB1[0] > pA3[0]) && (pA3[0] > pB2[0]))
					&& ((pB2[1] <= pB3[1]) && (pA2[0] <= pA1[0]))) {
				this._type = 5;
				this._hCrossSegMin = pB2[0];
				this._hCrossSegMax = pB1[0];
				this._vCrossSegMin = pA2[1];
				this._vCrossSegMax = pA3[1];
				this._distFromStartA = (((distToA1 + pA1[0]) - pA2[0]) + this._crossIndex[1])
						- this._vCrossSegMin;
				this._distFromStartB = (distToB1 + this._hCrossSegMax)
						- this._crossIndex[0];
			}
		} else if ((pA2[1] > pB1[1]) && (pB1[1] > pA3[1])) {
			this._crossIndex[0] = pA3[0];
			this._crossIndex[1] = pB1[1];
			this._crossSegIndexA = segA1A2Index + 1;
			this._crossSegIndexB = segB1B2Index;
			if ((pB1[0] < pA3[0]) && (pA3[0] < pB2[0])) {
				if ((pB3[1] <= pB2[1]) && (pA1[0] <= pA2[0])) {
					this._type = 2;
					this._hCrossSegMin = pB1[0];
					this._hCrossSegMax = pB2[0];
					this._vCrossSegMin = pA3[1];
					this._vCrossSegMax = pA2[1];
					this._distFromStartA = (((distToA1 + pA2[0]) - pA1[0]) + this._vCrossSegMax)
							- this._crossIndex[1];
					this._distFromStartB = (distToB1 + this._crossIndex[0])
							- this._hCrossSegMin;
				}
			} else if (((pB1[0] > pA3[0]) && (pA3[0] > pB2[0]))
					&& ((pB3[1] <= pB2[1]) && (pA2[0] <= pA1[0]))) {
				this._type = 7;
				this._hCrossSegMin = pB2[0];
				this._hCrossSegMax = pB1[0];
				this._vCrossSegMin = pA3[1];
				this._vCrossSegMax = pA2[1];
				this._distFromStartA = (((distToA1 + pA1[0]) - pA2[0]) + this._vCrossSegMax)
						- this._crossIndex[1];
				this._distFromStartB = (distToB1 + this._hCrossSegMax)
						- this._crossIndex[0];
			}
		} else if ((pB2[1] < pA1[1]) && (pA1[1] < pB3[1])) {
			this._crossIndex[0] = pB3[0];
			this._crossIndex[1] = pA1[1];
			this._crossSegIndexA = segA1A2Index;
			this._crossSegIndexB = segB1B2Index + 1;
			if ((pA1[0] < pB3[0]) && (pB3[0] < pA2[0])) {
				if ((pA2[1] <= pA3[1]) && (pB1[0] <= pB2[0])) {
					this._type = 0;
					this._hCrossSegMin = pA1[0];
					this._hCrossSegMax = pA2[0];
					this._vCrossSegMin = pB2[1];
					this._vCrossSegMax = pB3[1];
					this._distFromStartA = (distToA1 + this._crossIndex[0])
							- this._hCrossSegMin;
					this._distFromStartB = (((distToB1 + pB2[0]) - pB1[0]) + this._crossIndex[1])
							- this._vCrossSegMin;
				}
			} else if (((pA1[0] > pB3[0]) && (pB3[0] > pA2[0]))
					&& ((pA2[1] <= pA3[1]) && (pB2[0] <= pB1[0]))) {
				this._type = 5;
				this._hCrossSegMin = pA2[0];
				this._hCrossSegMax = pA1[0];
				this._vCrossSegMin = pB2[1];
				this._vCrossSegMax = pB3[1];
				this._distFromStartA = (distToA1 + this._hCrossSegMax)
						- this._crossIndex[0];
				this._distFromStartB = (((distToB1 + pB1[0]) - pB2[0]) + this._crossIndex[1])
						- this._vCrossSegMin;
			}
		} else if ((pB2[1] > pA1[1]) && (pA1[1] > pB3[1])) {
			this._crossIndex[0] = pB3[0];
			this._crossIndex[1] = pA1[1];
			this._crossSegIndexA = segA1A2Index;
			this._crossSegIndexB = segB1B2Index + 1;
			if ((pA1[0] < pB3[0]) && (pB3[0] < pA2[0])) {
				if ((pA3[1] <= pA2[1]) && (pB1[0] <= pB2[0])) {
					this._type = 2;
					this._hCrossSegMin = pA1[0];
					this._hCrossSegMax = pA2[0];
					this._vCrossSegMin = pB3[1];
					this._vCrossSegMax = pB2[1];
					this._distFromStartA = (distToA1 + this._crossIndex[0])
							- this._hCrossSegMin;
					this._distFromStartB = (((distToB1 + pB2[0]) - pB1[0]) + this._vCrossSegMax)
							- this._crossIndex[1];
				}
			} else if (((pA1[0] > pB3[0]) && (pB3[0] > pA2[0]))
					&& ((pA3[1] <= pA2[1]) && (pB2[0] <= pB1[0]))) {
				this._type = 7;
				this._hCrossSegMin = pA2[0];
				this._hCrossSegMax = pA1[0];
				this._vCrossSegMin = pB3[1];
				this._vCrossSegMax = pB2[1];
				this._distFromStartA = (distToA1 + this._hCrossSegMax)
						- this._crossIndex[0];
				this._distFromStartB = (((distToB1 + pB1[0]) - pB2[0]) + this._vCrossSegMax)
						- this._crossIndex[1];
			}
		}

		return (this._type != -1);

	}

	public void Dispose() {
		if (this._next != null) {
			this._next.Dispose();
		}
		this._crossIndex = null;
		this._next = null;

	}

	public Integer GetCrossContIndex(Integer dir) {
		if (dir == 0) {

			return this.GetHorizontalCrossContIndex();
		}

		return this.GetVerticalCrossContIndex();

	}

	public Integer GetCrossIndex(Integer dir) {

		return this._crossIndex[dir];

	}

	public Integer GetCrossSegIndexA() {

		return this._crossSegIndexA;

	}

	public Integer GetCrossSegIndexB() {

		return this._crossSegIndexB;

	}

	public Integer GetCrossType() {

		return this._type;

	}

	public Integer GetDistFromStartA() {

		return this._distFromStartA;

	}

	public Integer GetDistFromStartB() {

		return this._distFromStartB;

	}

	public Integer GetHorizontalCrossContIndex() {
		if ((this._type % 4) == 0 || (this._type % 4) == 2) {

			return this._hCrossSegMin;
		} else if ((this._type % 4) == 1 || (this._type % 4) == 3) {

			return this._hCrossSegMax;
		}

		return 0;

	}

	public Integer GetHorizontalFreeSegment(Integer[] limit) {
		if ((this._type % 2) == 0) {
			limit[0] = this._crossIndex[0] + 1;
			limit[1] = this._hCrossSegMax;
		} else {
			limit[0] = this._hCrossSegMin;
			limit[1] = this._crossIndex[0] - 1;
		}

		return this._crossIndex[1];

	}

	public Integer GetHorizontalTestSegment(Integer[] limit) {
		if ((this._type % 2) == 0) {
			limit[0] = this._crossIndex[0] + 1;
			limit[1] = this._hCrossSegMax;
		} else {
			limit[0] = this._hCrossSegMin;
			limit[1] = this._crossIndex[0] - 1;
		}
		if (((this._type % 4) != 0) && ((this._type % 4) != 1)) {

			return this._vCrossSegMax;
		}

		return this._vCrossSegMin;

	}

	public LLCrossPoint GetNext() {

		return this._next;

	}

	public Integer GetVerticalCrossContIndex() {
		if ((this._type % 4) == 0 || (this._type % 4) == 1) {

			return this._vCrossSegMax;
		} else if ((this._type % 4) == 2 || (this._type % 4) == 3) {

			return this._vCrossSegMin;
		}

		return 0;

	}

	public Integer GetVerticalFreeSegment(Integer[] limit) {
		if (((this._type % 4) == 0) || ((this._type % 4) == 1)) {
			limit[0] = this._vCrossSegMin;
			limit[1] = this._crossIndex[1] - 1;
		} else {
			limit[0] = this._crossIndex[1] + 1;
			limit[1] = this._vCrossSegMax;
		}

		return this._crossIndex[0];

	}

	public Integer GetVerticalTestSegment(Integer[] limit) {
		if (((this._type % 4) == 0) || ((this._type % 4) == 1)) {
			limit[0] = this._vCrossSegMin;
			limit[1] = this._crossIndex[1] - 1;
		} else {
			limit[0] = this._crossIndex[1] + 1;
			limit[1] = this._vCrossSegMax;
		}
		if ((this._type % 2) == 0) {

			return this._hCrossSegMax;
		}

		return this._hCrossSegMin;

	}

	public Boolean IsOppositeFlow() {

		return (this._type >= 8);

	}

	public Boolean LetSurvive(LLCrossPoint point) {
		Integer crossIndex = point.GetCrossIndex(0);
		Integer num2 = point.GetCrossIndex(1);
		if (crossIndex == this._crossIndex[0]) {
			if (((this._type % 4) == 0) || ((this._type % 4) == 1)) {
				if (((this._vCrossSegMin + 1) <= num2)
						&& (num2 <= (this._crossIndex[1] - 1))) {

					return false;
				}
			} else if (((this._crossIndex[1] + 1) <= num2)
					&& (num2 <= (this._vCrossSegMax - 1))) {

				return false;
			}
		}
		if (num2 == this._crossIndex[1]) {
			if ((this._type % 2) == 0) {
				if (((this._crossIndex[0] + 1) <= crossIndex)
						&& (crossIndex <= (this._hCrossSegMax - 1))) {

					return false;
				}
			} else if (((this._hCrossSegMin + 1) <= crossIndex)
					&& (crossIndex <= (this._crossIndex[0] - 1))) {

				return false;
			}
		}

		return true;

	}

	public void MakeUnfeasable() {
		this._type = -1;

	}

	public Boolean NeedBypassIfFirst(LLLink link, Integer crossSegIndex) {
		if ((this._type % 4) == 0) {
			if (link.GetSegmentDir(crossSegIndex) != 0) {
				if (link.GetSegments()[crossSegIndex] > 0) {

					return true;
				}

			}
			if (link.GetSegments()[crossSegIndex] >= 0) {
			}

			return true;
		} else if ((this._type % 4) == 1) {
			if (link.GetSegmentDir(crossSegIndex) != 0) {
				if (link.GetSegments()[crossSegIndex] > 0) {

					return true;
				}
				return false;
			}
			if (link.GetSegments()[crossSegIndex] <= 0) {
				return false;
			}

			return true;
		} else if ((this._type % 4) == 2) {
			if (link.GetSegmentDir(crossSegIndex) != 0) {
				if (link.GetSegments()[crossSegIndex] < 0) {

					return true;
				}
				return false;
			}
			if (link.GetSegments()[crossSegIndex] >= 0) {
				return false;
			}

			return true;
		} else if ((this._type % 4) == 3) {
			if (link.GetSegmentDir(crossSegIndex) != 0) {
				if (link.GetSegments()[crossSegIndex] < 0) {

					return true;
				}
				return false;
			}
			if (link.GetSegments()[crossSegIndex] <= 0) {
				return false;
			}

			return true;
		} else {

			return false;
		}

	}

	public void SetNext(LLCrossPoint next) {
		this._next = next;

	}

}