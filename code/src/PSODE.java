import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class PSODE {
	public static Graph g;
	public static final int NP = 100; // Có 100 cá thể
	public static final int Nmax = 10; // Kích trước kho lưu trữ không bị chi phối
	public PSODEPath[] particles = new PSODEPath[NP];
	public static int numR;
	public static double R; // ban kinh ban do
	public static Point startPoint;
	public static Point endPoint;
	public static final int ITmax = 100; // Số vòng lặp tối đa
	public static final double wMax = 0.9;
	public static final double wMin = 0.2;
	public static double w;
	public static double r1;
	public static double r2;
	public static double r;
	public static double pm;
	public static final double c1 = 2;
	public static final double c2 = 2;
	public static final double V_MAX = 30;
	public static final double V_MIN = -30;
	public double[][] vValue;
	public static PSODEPath[] pBestParticles = new PSODEPath[NP];
	public static PSODEPath gBestParticle;
	static final double maxAngle = 30;
	static final double minAngle = -30;
	static Random rd = new Random();
	static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	static DecimalFormat df = (DecimalFormat) nf;
	public PSODEPath[] NaParticles = new PSODEPath[Nmax];
	public PSODEPath[] NbParticles = new PSODEPath[Nmax];

	// Hàm làm tròn số
	static int roundDown(double number) {
		double place = 1;
		double result = number / place;
		result = Math.floor(result);
		result *= place;
		return (int) result;
	}

	// Tính số khoảng m
	public static int getNumR(double R, Point start, Point end) {
		double a2, b2, distance;
		int num;
		a2 = Math.pow(Math.abs(start.x - end.x), 2);
		b2 = Math.pow(Math.abs(start.y - end.y), 2);
		distance = Math.sqrt(a2 + b2);
		System.out.println("Start to end point distance: " + distance);
		num = roundDown(distance / R);
		return num;
	}

	// Hàm khởi tạo
	@SuppressWarnings("unused")
	public void initialize() {
		numR = getNumR(R, startPoint, endPoint);
		@SuppressWarnings("unused")
		double tAngle;
		Point tPoint;
		System.out.println("Population size: " + particles.length);// Số lượng hạt
		for (int i = 0; i < NP; i++) {
			PSODEPath newPath = new PSODEPath(numR);
			for (int j = 0; j < PSODEPath.n; j++) {
				// Khởi tạo vị trí ban đầu của các node
				newPath.angles[j] = rd.nextDouble() * ((maxAngle - minAngle) + 1) + minAngle;
				newPath.points[j] = PSODEPath.convertAtoP(newPath.angles[j], (j + 1) * PSODE.R, PSODE.startPoint, PSODE.endPoint);
			}
			particles[i] = newPath;
		}
	}

	// So sánh 2 hạt, hạt nào là tốt nhất
	public boolean compare(PSODEPath particle1, PSODEPath particle2) {
		if (particle1.points[0] == null) {
			return false;
		} else if (particle2.points[0] == null) {
			return true;
		} else if (particle1.pathDistance(PSO.R) <= particle2.pathDistance(PSODE.R))
		{
			return true; // Thỏa mãn 3 tiêu chí thì trả về true tức hạt 1 trội hơn hạt 2
		} else if (particle1.pathDistance(PSODE.R) > particle2.pathDistance(PSODE.R))
{
			return false; // hạt 2 trội hơn hạt 1
		} else {
			double f = rd.nextDouble();
			if (f <= 0.5) {
				return true;
			} else
				return false;
		}
	}

	// Kiểm tra hạt trội hơn
	public boolean checkDominate(PSODEPath par1, PSODEPath par2) {
		if (par2.points[0] == null) {
			return true;
		} else if (par1.pathDistance(PSODE.R) <= par2.pathDistance(PSODE.R))
			{
			return true; // Đúng trả về hạt 2 trội hơn hạt 1
		} else
			return false;
	}

	// Xét tiêu chí đường đi
	public int[] rank(PSODEPath[] NParticle, int type) {
		int len = NParticle.length;
		int[] rank = new int[len];
		double[] Nobj = new double[len];
		int count;
		if (type == 1) {
			for (int i = 0; i < len; i++) {
				if (NParticle[i].points[0] != null) {
					Nobj[i] = NParticle[i].pathDistance(PSODE.R);
				} else
					Nobj[i] = Double.POSITIVE_INFINITY;
				;
			}
		} 
		for (int i = 0; i < len; i++) {
			count = 0;
			for (int j = 0; j < len; j++) {
				if (j != i && Nobj[j] >= Nobj[i]) {
					count++;
				}
				rank[i] = len - count - 1;
				for (int k = 0; k < i; k++) {
					if (rank[k] == rank[i]) {
						rank[i]++;
					}
				}
			}
		}
		return rank;
	}

	// Đảo ngược mảng
	public int[] reverseRank(int[] rank) {
		int len = rank.length;
		int[] reverseRank = new int[len];
		int temp;
		for (int i = 0; i < len; i++) {
			temp = rank[i];
			reverseRank[temp] = i;
		}
		return reverseRank;
	}

	// Tính khoảng cách hội tụ
	public double[] crowdingDistance(PSODEPath[] NParticle) {
		int len = NParticle.length;
		double[] CD = new double[len];
		double[] Ndis = new double[len];
		int[] rankDistance = new int[len];
		int[] reRankDistance = new int[len];
		rankDistance = rank(NParticle, 1);
		reRankDistance = reverseRank(rankDistance);
		for (int i = 0; i < len; i++) {
			CD[i] = 0;
			if (NParticle[i].points[0] != null) {
				Ndis[i] = NParticle[i].pathDistance(PSODE.R);
			}
		}
		int index = 0;
		for (int i = 0; i < len; i++) {
			if (NParticle[i].points[0] == null) {
				index++;
			}
		}
		for (int i = 0; i < len; i++) {
			if (rankDistance[i] == 0 || rankDistance[i] == (len - index - 1)) {
				CD[i] += 999999999.9;
			}
			if (NParticle[i].points[0] == null) {
				CD[i] = 0;
			} else if (rankDistance[i] != 0 && rankDistance[i] != (len - index - 1))
					{
				CD[i] = CD[i] + (Ndis[reRankDistance[rankDistance[i] + 1]] - Ndis[reRankDistance[rankDistance[i] - 1]])
						/ (Ndis[reRankDistance[len - index - 1]] - Ndis[reRankDistance[0]]);
			}
		}
		return CD;
	}
	
	// Phát hiện va chạm
	boolean pathDetectCollision(PSODEPath pa) {
		boolean colli = false;
		for (int i = 0; i < PSODEPath.n; i++) {
			if (i == 0) {
				if (CollisionDetection.graphLine(g, pa.points[i], PSODE.startPoint)) {
					colli = true;
				}
			} else if (i == PSODEPath.n - 1) {
				if (CollisionDetection.graphLine(g, pa.points[i - 1], pa.points[i])
						|| CollisionDetection.graphLine(g, pa.points[i], PSODE.endPoint)) {
					colli = true;
				}
			} else {
				if (CollisionDetection.graphLine(g, pa.points[i - 1], pa.points[i])) {
					colli = true;
				}
			}
		}
		return colli;
	}

	// Khởi tạo không gian lưu trữ
	public void initializeNaNb() {
		for (int j = 0; j < Nmax; j++) {
			NaParticles[j] = new PSODEPath(PSODE.numR);
			NbParticles[j] = new PSODEPath(PSODE.numR);
		}
		for (int i = 0; i < NP; i++) {
			// Kiểm tra cá thể nếu cá thể i va chạm với cnv thì cho vào kho lưu trữ Nb
			if (pathDetectCollision(particles[i]) == false) {
				addNArchive(particles[i], NaParticles);
			} else if (pathDetectCollision(particles[i]) == true) {
				addNArchive(particles[i], NbParticles);
			}
		}
	}

	// Thêm vào mảng Na
	public void addNArchive(PSODEPath par, PSODEPath[] NaParticles) {
		boolean check = false;
		boolean check2 = false;
		int breakPoint = 100;
		PSODEPath[] newPar = new PSODEPath[Nmax + 1];
		double[] CD = new double[Nmax + 1];
		double worstCD;
		int worstCDpos;
		boolean checkNull = true;
		for (int i = 0; i < NaParticles.length; i++) {
			if (NaParticles[i].points[0] != null) {
				checkNull = false;
			}
		}
		if (checkNull == true) { 
			for (int k = 0; k < PSODE.numR; k++) {
				NaParticles[0].angles[k] = par.angles[k];
				NaParticles[0].points[k] = new Point(par.points[k].x, par.points[k].y);
			}
		} else {
			int replace = 0;
			for (int j = 0; j < Nmax; j++) {
				if (NaParticles[j].points[0] == null) {
					breakPoint = j;
				} else if (checkDominate(NaParticles[j], par) == true) {
					// Kiểm tra đường đi j có trội hơn đường đi sắp thêm vào
					check = true;
					break;
				} else if (checkDominate(par, NaParticles[j]) == true) {
					// Kiểm tra đường đi chuẩn bị thêm vào có trội hơn đường đi j không
					check2 = true;
					NaParticles[j] = new PSODEPath(PSODE.numR);
					replace = j;
				}
			}

			if (check == true) {
			}
			else {
				if (check2 == true) { 
					for (int k = 0; k < PSODE.numR; k++) {
						// Nếu đường đi chuẩn bị thêm vào trội hơn thì cho vào tập
						NaParticles[replace].angles[k] = par.angles[k];
						NaParticles[replace].points[k] = new Point(par.points[k].x, par.points[k].y);
					}
				} else { 
					if (breakPoint != 100) {
						for (int k = 0; k < PSODE.numR; k++) {
							NaParticles[breakPoint].angles[k] = par.angles[k];
							NaParticles[breakPoint].points[k] = new Point(par.points[k].x, par.points[k].y);
						}
					} else { 
						for (int i = 0; i < Nmax; i++) {
							newPar[i] = new PSODEPath(PSODE.numR);
							for (int k = 0; k < PSODE.numR; k++) {
								newPar[i].angles[k] = NaParticles[i].angles[k];
								newPar[i].points[k] = new Point(NaParticles[i].points[k].x, NaParticles[i].points[k].y);
							}
						}
						newPar[Nmax] = new PSODEPath(PSODE.numR);
						for (int k = 0; k < PSODE.numR; k++) {
							newPar[Nmax].angles[k] = par.angles[k];
							newPar[Nmax].points[k] = new Point(par.points[k].x, par.points[k].y);
						}
						CD = crowdingDistance(newPar);
						worstCD = CD[0];
						worstCDpos = 0;
						for (int i = 0; i < NaParticles.length + 1; i++) {
							if (CD[i] < worstCD) {
								worstCD = CD[i];
								worstCDpos = i;
							}
						}
						if (worstCDpos == NaParticles.length) {
						} else {
							for (int k = 0; k < PSODE.numR; k++) {
								NaParticles[worstCDpos].angles[k] = par.angles[k];
								NaParticles[worstCDpos].points[k] = new Point(par.points[k].x, par.points[k].y);
							}
						}
					}
				}
			}
		}
	}

	

	
	public void getVelocity() {
		vValue = new double[NP][PSODEPath.n];
		for (int i = 0; i < NP; i++) {
			for (int j = 0; j < PSODEPath.n; j++) {
				vValue[i][j] = rd.nextDouble() * ((V_MAX - V_MIN) + 1) + V_MIN;
			}
		}
		return;
	}

	// Đột biến
	public PSODEPath mutation(PSODEPath particle) {
		int x1 = rd.nextInt(Nmax - 1);
		int x2 = rd.nextInt(Nmax - 1);
		double f = rd.nextDouble();
		for (int h = 0; h < PSODEPath.n; h++) {
			particle.angles[h] = particle.angles[h] + f * (NaParticles[x1].angles[h] - NaParticles[x2].angles[h]);
			if (particle.angles[h] > maxAngle) {
				particle.angles[h] = maxAngle;
			} else if (particle.angles[h] < minAngle) {
				particle.angles[h] = minAngle;
			}
			particle.points[h] = PSODEPath.convertAtoP(particle.angles[h], (h + 1) * PSODE.R, PSODE.startPoint, PSODE.endPoint);
		}
		return particle;
	}

	public void gBestSelection(int i) {
		double[] CD = new double[Nmax];
		double bestCD;
		int bestCDpos;
		boolean NaNull = true, NbNull = true;
		for (int k = 0; k < NaParticles.length; k++) {
			if (NaParticles[k].points[0] != null) {
				NaNull = false;
			}
		}
		for (int k = 0; k < NbParticles.length; k++) {
			if (NbParticles[k].points[0] != null) {
				NbNull = false;
			}
		}
		if (NaNull == true) {
			CD = crowdingDistance(NbParticles);
			bestCD = CD[0];
			bestCDpos = 0;
			for (int i1 = 0; i1 < Nmax; i1++) {
				if (CD[i1] > bestCD) {
					bestCD = CD[i1];
					bestCDpos = i1;
				}
			}
			for (int j = 0; j < PSODE.numR; j++) {
				gBestParticle.angles[j] = NbParticles[bestCDpos].angles[j];
				gBestParticle.points[j] = new Point(NbParticles[bestCDpos].points[j].x,
						NbParticles[bestCDpos].points[j].y);
			}
		} else if (NbNull == true) { 
			CD = crowdingDistance(NaParticles);
			bestCD = CD[0];
			bestCDpos = 0;
			for (int i1 = 0; i1 < Nmax; i1++) {
				if (CD[i1] > bestCD) {
					bestCD = CD[i1];
					bestCDpos = i1;
				}
			}
			for (int j = 0; j < PSODE.numR; j++) {
				gBestParticle.angles[j] = NaParticles[bestCDpos].angles[j];
				gBestParticle.points[j] = new Point(NaParticles[bestCDpos].points[j].x,
						NaParticles[bestCDpos].points[j].y);
			}
		} else {
			double ps = 0.5 - 0.5 * i / ITmax;
			double r = rd.nextDouble();
			if (ps < r) {
				CD = crowdingDistance(NaParticles);
				bestCD = CD[0];
				bestCDpos = 0;
				for (int i1 = 0; i1 < Nmax; i1++) {
					if (CD[i1] > bestCD) {
						bestCD = CD[i1];
						bestCDpos = i1;
					}
				}
				for (int j = 0; j < PSODE.numR; j++) {
					gBestParticle.angles[j] = NaParticles[bestCDpos].angles[j];
					gBestParticle.points[j] = new Point(NaParticles[bestCDpos].points[j].x,
							NaParticles[bestCDpos].points[j].y);
				}
			} else {
				CD = crowdingDistance(NbParticles);
				bestCD = CD[0];
				bestCDpos = 0;
				for (int i1 = 0; i1 < Nmax; i1++) {
					if (CD[i1] > bestCD) {
						bestCD = CD[i1];
						bestCDpos = i1;
					}
				}
				for (int j = 0; j < PSODE.numR; j++) {
					gBestParticle.angles[j] = NbParticles[bestCDpos].angles[j];
					gBestParticle.points[j] = new Point(NbParticles[bestCDpos].points[j].x,
							NbParticles[bestCDpos].points[j].y);
				}
			}
		}
	}

	public void PSO(String fileName) throws IOException {
		initialize();
		for (int i = 0; i < NP; i++) {
			pBestParticles[i] = new PSODEPath(PSODE.numR);
			for (int j = 0; j < PSODE.numR; j++) {
				pBestParticles[i].angles[j] = particles[i].angles[j];
				pBestParticles[i].points[j] = new Point(particles[i].points[j].x, particles[i].points[j].y);
			}
		}

		initializeNaNb();
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNa " + i + ": ");
			if (NaParticles[i].points[0] != null) {
				System.out.print("Obj: " + NaParticles[i].pathDistance(PSODE.R));
				for (int j = 0; j < PSODE.numR; j++) {
					System.out.print("(" + df.format(NaParticles[i].points[j].x) + ", "
							+ df.format(NaParticles[i].points[j].y) + ") ");
				}
			}
		}
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNb " + i + ": ");
			if (NbParticles[i].points[0] != null) {
				System.out.print("Obj: " + NbParticles[i].pathDistance(PSODE.R));
				for (int j = 0; j < PSODE.numR; j++) {
					System.out.print("(" + df.format(NbParticles[i].points[j].x) + ", "
							+ df.format(NbParticles[i].points[j].y) + ") ");
				}
			}
		}
		gBestParticle = new PSODEPath(PSODE.numR);
		gBestSelection(0);

		System.out.println("\n\nBest Value: " + gBestParticle.pathDistance(PSODE.R) + ", ");
		for (int j = 0; j < PSODE.numR; j++) {
			System.out.print(
					"(" + df.format(gBestParticle.points[j].x) + ", " + df.format(gBestParticle.points[j].y) + ") ");
		}
		boolean parColli, pBestColli;

		// PSO
		for (int i = 0; i < ITmax; i++) {
			w = wMax - ((wMax - wMin) * i / ITmax);
			for (int j = 0; j < NP; j++) {
				for (int h = 0; h < PSODEPath.n; h++) {
					int x1 = rd.nextInt(Nmax - 1);
					int x2 = rd.nextInt(Nmax - 1);
					double f = rd.nextDouble();
					particles[j].angles[h] = particles[j].angles[h] + f * (NaParticles[x1].angles[h] - NaParticles[x2].angles[h]);
					if (particles[j].angles[h] > maxAngle) {
						particles[j].angles[h] = maxAngle;
					} else if (particles[j].angles[h] < minAngle) {
						particles[j].angles[h] = minAngle;
					}
					particles[j].points[h] = PSODEPath.convertAtoP(particles[j].angles[h], (h + 1) * PSODE.R, PSODE.startPoint, PSODE.endPoint);
				}
				parColli = pathDetectCollision(particles[j]);
				pBestColli = pathDetectCollision(pBestParticles[j]);
				if (parColli && pBestColli) {
					if (compare(particles[j], pBestParticles[j])) {
						for (int h = 0; h < PSODEPath.n; h++) {
							pBestParticles[j].angles[h] = particles[j].angles[h];
							pBestParticles[j].points[h].x = particles[j].points[h].x;
							pBestParticles[j].points[h].y = particles[j].points[h].y;
						}
						addNArchive(pBestParticles[j], NbParticles);
					}
				} else if (!parColli && !pBestColli) {
					if (compare(particles[j], pBestParticles[j])) {
						for (int h = 0; h < PSODEPath.n; h++) {
							pBestParticles[j].angles[h] = particles[j].angles[h];
							pBestParticles[j].points[h].x = particles[j].points[h].x;
							pBestParticles[j].points[h].y = particles[j].points[h].y;
						}
						addNArchive(pBestParticles[j], NaParticles);
					}
				} else {
					if (!parColli && pBestColli) {
						for (int h = 0; h < PSODEPath.n; h++) {
							pBestParticles[j].angles[h] = particles[j].angles[h];
							pBestParticles[j].points[h].x = particles[j].points[h].x;
							pBestParticles[j].points[h].y = particles[j].points[h].y;
						}
						addNArchive(pBestParticles[j], NaParticles);
					}
				}
			}

			// Select GBest
			System.out.println("\n");
			gBestSelection(i);

			System.out.println("Epochs " + i + " Best Value: " + gBestParticle.pathDistance(PSODE.R) + ", ");
			System.out.print("Epochs " + i + " Best Particles: (");
			for (int j = 0; j < PSODE.numR; j++) {
				System.out.print("(" + df.format(gBestParticle.points[j].x) + ", "
						+ df.format(gBestParticle.points[j].y) + ") ");
			}
		}

		// Print Na, Nb
		System.out.println("");
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNa " + i + ": ");
			if (NaParticles[i].points[0] != null) {
				System.out.print("Obj: " + NaParticles[i].pathDistance(PSODE.R));
				for (int j = 0; j < PSODE.numR; j++) {
					System.out.print("(" + df.format(NaParticles[i].points[j].x) + ", "
							+ df.format(NaParticles[i].points[j].y) + ") ");
				}
			}
		}
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNb " + i + ": ");
			if (NbParticles[i].points[0] != null) {
				System.out.print("Obj: " + NbParticles[i].pathDistance(PSODE.R));
				for (int j = 0; j < PSODE.numR; j++) {
					System.out.print("(" + df.format(NbParticles[i].points[j].x) + ", "
							+ df.format(NbParticles[i].points[j].y) + ") ");
				}
			}
		}

		// Print gBest
		System.out.println("");
		System.out.println("Best Value PSODE: " + gBestParticle.pathDistance(PSODE.R));
		System.out.println("Best Value Distance PSODE: " + gBestParticle.pathDistance(R));
		System.out.print("Best Particle PSODE: (");
		for (int j = 0; j < PSODE.numR; j++) {
			System.out.print(
					"(" + df.format(gBestParticle.points[j].x) + ", " + df.format(gBestParticle.points[j].y) + ") ");
		}
		System.out.println(")");

		FileWriter results = new FileWriter(fileName, true);
		results.write("PSODE" + "\n");
		results.write("Best Distance PSODE: " + gBestParticle.pathDistance(PSODE.R) + "\n");
		for (int j = 0; j < PSODE.numR; j++) {
			results.write(
					"(" + df.format(gBestParticle.points[j].x) + ", " + df.format(gBestParticle.points[j].y) + ") ");
		}
		results.write("\n");
		results.write(" ");
		results.close();
	}
}