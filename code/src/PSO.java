import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class PSO {
	public static Graph g;
	public static final int NP = 100; 
	public static final int Nmax = 10; 
	public PSOPath[] particles = new PSOPath[NP];
	public static int numR; 
	public static double R;
	public static Point startPoint;
	public static Point endPoint;
	public static final int ITmax = 100; 
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
	public static PSOPath[] pBestParticles = new PSOPath[NP];
	public static PSOPath gBestParticle;
	static final double maxAngle = 30;
	static final double minAngle = -30;
	static Random rd = new Random();
	static NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	static DecimalFormat df = (DecimalFormat) nf;
	public PSOPath[] NaParticles = new PSOPath[Nmax];
	public PSOPath[] NbParticles = new PSOPath[Nmax];

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
	public void initialize() {
		numR = getNumR(R, startPoint, endPoint);
		double tAngle;
		Point tPoint;
		System.out.println("Population size: " + particles.length);// Số lượng hạt
		for (int i = 0; i < NP; i++) {
			PSOPath newPath = new PSOPath(numR);
			for (int j = 0; j < PSOPath.n; j++) {
				if (j == 0) {
					newPath.angles[j] = rd.nextDouble() * ((maxAngle - minAngle) + 1) + minAngle;
					newPath.points[j] = PSOPath.convertAtoP(newPath.angles[j], (j + 1) * PSO.R, PSO.startPoint,
							PSO.endPoint);
				} else if (j > 0 && j < PSOPath.n - 1) {
					tAngle = rd.nextDouble() * ((maxAngle - minAngle) + 1) + minAngle;
					tPoint = PSOPath.convertAtoP(tAngle, (j + 1) * PSO.R, PSO.startPoint, PSO.endPoint);
					newPath.angles[j] = tAngle;
					newPath.points[j] = tPoint;
				} else {
					tAngle = rd.nextDouble() * ((maxAngle - minAngle) + 1) + minAngle;
					tPoint = PSOPath.convertAtoP(tAngle, (j + 1) * PSO.R, PSO.startPoint, PSO.endPoint);
					newPath.angles[j] = tAngle;
					newPath.points[j] = tPoint;
				}
			}
			particles[i] = newPath;
		}
	}

	// So sánh 2 hạt 
	public boolean compare(PSOPath particle1, PSOPath particle2) {
		if (particle1.points[0] == null) {
			return false;
		} else if (particle2.points[0] == null) {
			return true;
		} else if (particle1.pathDistance(PSO.R) <= particle2.pathDistance(PSO.R))
		{
			return true;
		} else if (particle1.pathDistance(PSO.R) > particle2.pathDistance(PSO.R))
		{
			return false; 
		} else {
			double f = rd.nextDouble();
			if (f <= 0.5) {
				return true;
			} else
				return false;
		}
	}

	// Kiểm tra hạt trội hơn
	public boolean checkDominate(PSOPath par1, PSOPath par2) {
		if (par2.points[0] == null) {
			return true;
		} else if (par1.pathDistance(PSO.R) <= par2.pathDistance(PSO.R))
			{
			return true;
		} else
			return false;
	}

	// Xét tiêu chí đánh giá
	public int[] rank(PSOPath[] NParticle, int type) {
		int len = NParticle.length;
		int[] rank = new int[len];
		double[] Nobj = new double[len];
		int count;
		if (type == 1) {
			for (int i = 0; i < len; i++) {
				if (NParticle[i].points[0] != null) {
					Nobj[i] = NParticle[i].pathDistance(PSO.R);
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

	public double[] crowdingDistance(PSOPath[] NParticle) {
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
				Ndis[i] = NParticle[i].pathDistance(PSO.R);
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
	boolean pathDetectCollision(PSOPath pa) {
		boolean colli = false;
		for (int i = 0; i < PSOPath.n; i++) {
			if (i == 0) {
				if (CollisionDetection.graphLine(g, pa.points[i], PSO.startPoint)) {
					colli = true;
				}
			} else if (i == PSOPath.n - 1) {
				if (CollisionDetection.graphLine(g, pa.points[i - 1], pa.points[i])
						|| CollisionDetection.graphLine(g, pa.points[i], PSO.endPoint)) {
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
			NaParticles[j] = new PSOPath(PSO.numR);
			NbParticles[j] = new PSOPath(PSO.numR);
		}
		for (int i = 0; i < NP; i++) {
			if (pathDetectCollision(particles[i]) == false) {
				addNArchive(particles[i], NaParticles);
			} else if (pathDetectCollision(particles[i]) == true) {
				addNArchive(particles[i], NbParticles);
			}
		}
	}

	// Thêm vào mảng Na
	public void addNArchive(PSOPath par, PSOPath[] NaParticles) {
		boolean check = false;
		boolean check2 = false;
		int breakPoint = 100;
		PSOPath[] newPar = new PSOPath[Nmax + 1];
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
			for (int k = 0; k < PSO.numR; k++) {
				NaParticles[0].angles[k] = par.angles[k];
				NaParticles[0].points[k] = new Point(par.points[k].x, par.points[k].y);
			}
		} else { 
			int replace = 0;
			for (int j = 0; j < Nmax; j++) {
				if (NaParticles[j].points[0] == null) {
					breakPoint = j;
				} else if (checkDominate(NaParticles[j], par) == true) {
					check = true; 
					break;
				} else if (checkDominate(par, NaParticles[j]) == true) { 
					check2 = true;
					NaParticles[j] = new PSOPath(PSO.numR);
					replace = j;
				}
			}

			if (check == true) {
			}else {
				if (check2 == true) {
					for (int k = 0; k < PSO.numR; k++) {
						NaParticles[replace].angles[k] = par.angles[k];
						NaParticles[replace].points[k] = new Point(par.points[k].x, par.points[k].y);
					}
				} else {
					if (breakPoint != 100) { 
						for (int k = 0; k < PSO.numR; k++) {
							NaParticles[breakPoint].angles[k] = par.angles[k];
							NaParticles[breakPoint].points[k] = new Point(par.points[k].x, par.points[k].y);
						}
					} else {
						for (int i = 0; i < Nmax; i++) {
							newPar[i] = new PSOPath(PSO.numR);
							for (int k = 0; k < PSO.numR; k++) {
								newPar[i].angles[k] = NaParticles[i].angles[k];
								newPar[i].points[k] = new Point(NaParticles[i].points[k].x, NaParticles[i].points[k].y);
							}
						}
						newPar[Nmax] = new PSOPath(PSO.numR);
						for (int k = 0; k < PSO.numR; k++) {
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
							for (int k = 0; k < PSO.numR; k++) {
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
		vValue = new double[NP][PSOPath.n];
		for (int i = 0; i < NP; i++) {
			for (int j = 0; j < PSOPath.n; j++) {
				vValue[i][j] = rd.nextDouble() * ((V_MAX - V_MIN) + 1) + V_MIN;
			}
		}
		return;
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
			for (int j = 0; j < PSO.numR; j++) {
				gBestParticle.angles[j] = NbParticles[bestCDpos].angles[j];
				gBestParticle.points[j] = new Point(NbParticles[bestCDpos].points[j].x,
						NbParticles[bestCDpos].points[j].y);
			}
		} else if (NbNull == true) { // Sai
			CD = crowdingDistance(NaParticles);
			bestCD = CD[0];
			bestCDpos = 0;
			for (int i1 = 0; i1 < Nmax; i1++) {
				if (CD[i1] > bestCD) {
					bestCD = CD[i1];
					bestCDpos = i1;
				}
			}
			for (int j = 0; j < PSO.numR; j++) {
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
				for (int j = 0; j < PSO.numR; j++) {
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
				for (int j = 0; j < PSO.numR; j++) {
					gBestParticle.angles[j] = NbParticles[bestCDpos].angles[j];
					gBestParticle.points[j] = new Point(NbParticles[bestCDpos].points[j].x,
							NbParticles[bestCDpos].points[j].y);
				}
			}
		}
	}

	public void PSO(String fileName) throws IOException {
		initialize();

		// initialize pBest
		for (int i = 0; i < NP; i++) {
			pBestParticles[i] = new PSOPath(PSO.numR);
			for (int j = 0; j < PSO.numR; j++) {
				pBestParticles[i].angles[j] = particles[i].angles[j];
				pBestParticles[i].points[j] = new Point(particles[i].points[j].x, particles[i].points[j].y);
			}
		}

		// initialize Na, Nb
		initializeNaNb();
		
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNa " + i + ": ");
			if (NaParticles[i].points[0] != null) {
				System.out.print("Obj: " + NaParticles[i].pathDistance(PSO.R));
				for (int j = 0; j < PSO.numR; j++) {
					System.out.print("(" + df.format(NaParticles[i].points[j].x) + ", "
							+ df.format(NaParticles[i].points[j].y) + ") ");
				}
			}
		}
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNb " + i + ": ");
			if (NbParticles[i].points[0] != null) {
				System.out.print("Obj: " + NbParticles[i].pathDistance(PSO.R));
				for (int j = 0; j < PSO.numR; j++) {
					System.out.print("(" + df.format(NbParticles[i].points[j].x) + ", "
							+ df.format(NbParticles[i].points[j].y) + ") ");
				}
			}
		}

		// initialize gBest
		gBestParticle = new PSOPath(PSO.numR);
		gBestSelection(0);

		System.out.println("\n\nBest Value: " + gBestParticle.pathDistance(PSO.R));
		for (int j = 0; j < PSO.numR; j++) {
			System.out.print(
					"(" + df.format(gBestParticle.points[j].x) + ", " + df.format(gBestParticle.points[j].y) + ") ");
		}

		getVelocity();
		boolean parColli, pBestColli;

		// PSO
		for (int i = 0; i < ITmax; i++) {
			w = wMax - ((wMax - wMin) * i / ITmax);
			for (int j = 0; j < NP; j++) {
				for (int h = 0; h < PSOPath.n; h++) {
					int x1 = rd.nextInt(Nmax - 1);
					int x2 = rd.nextInt(Nmax - 1);
					double f = rd.nextDouble();
					particles[j].angles[h] = particles[j].angles[h]
							+ f * (NaParticles[x1].angles[h] - NaParticles[x2].angles[h]);
					if (particles[j].angles[h] > maxAngle) {
						particles[j].angles[h] = maxAngle;
					} else if (particles[j].angles[h] < minAngle) {
						particles[j].angles[h] = minAngle;
					}
					particles[j].points[h] = PSOPath.convertAtoP(particles[j].angles[h], (h + 1) * PSO.R, PSO.startPoint,
							PSO.endPoint);
				}
				// pBest
				parColli = pathDetectCollision(particles[j]);
				pBestColli = pathDetectCollision(pBestParticles[j]);
				if (parColli && pBestColli) {
					if (compare(particles[j], pBestParticles[j])) {
						for (int h = 0; h < PSOPath.n; h++) {
							pBestParticles[j].angles[h] = particles[j].angles[h];
							pBestParticles[j].points[h].x = particles[j].points[h].x;
							pBestParticles[j].points[h].y = particles[j].points[h].y;
						}
						addNArchive(pBestParticles[j], NbParticles);
					}
				} else if (!parColli && !pBestColli) {
					if (compare(particles[j], pBestParticles[j])) {
						for (int h = 0; h < PSOPath.n; h++) {
							pBestParticles[j].angles[h] = particles[j].angles[h];
							pBestParticles[j].points[h].x = particles[j].points[h].x;
							pBestParticles[j].points[h].y = particles[j].points[h].y;
						}
						addNArchive(pBestParticles[j], NaParticles);
					}
				} else {
					if (!parColli && pBestColli) {
						for (int h = 0; h < PSOPath.n; h++) {
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

			System.out.println("Epochs " + i + " Best Value: " + gBestParticle.pathDistance(PSO.R) + ", ");
			System.out.print("Epochs " + i + " Best Particles: (");
			for (int j = 0; j < PSO.numR; j++) {
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
				System.out.print("Obj: " + NaParticles[i].pathDistance(PSO.R));
				for (int j = 0; j < PSO.numR; j++) {
					System.out.print("(" + df.format(NaParticles[i].points[j].x) + ", "
							+ df.format(NaParticles[i].points[j].y) + ") ");
				}
			}
		}
		System.out.println("");
		for (int i = 0; i < Nmax; i++) {
			System.out.print("\nNb " + i + ": ");
			if (NbParticles[i].points[0] != null) {
				System.out.print("Obj: " + NbParticles[i].pathDistance(PSO.R));
				for (int j = 0; j < PSO.numR; j++) {
					System.out.print("(" + df.format(NbParticles[i].points[j].x) + ", "
							+ df.format(NbParticles[i].points[j].y) + ") ");
				}
			}
		}

		// Print gBest
		System.out.println("");
		System.out.println("Best Value: " + gBestParticle.pathDistance(PSO.R));
		System.out.println("Best Distance Value PSO: " + gBestParticle.pathDistance(R));
		System.out.print("Best Particles PSO: (");
		for (int j = 0; j < PSO.numR; j++) {
			System.out.print(
					"(" + df.format(gBestParticle.points[j].x) + ", " + df.format(gBestParticle.points[j].y) + ") ");
		}
		System.out.println(")");

		FileWriter results = new FileWriter(fileName, true);
		results.write("PSO" + "\n");
		results.write("Best Distance PSO: " + gBestParticle.pathDistance(PSO.R) + "\n");
		for (int j = 0; j < PSO.numR; j++) {
			results.write(
					"(" + df.format(gBestParticle.points[j].x) + ", " + df.format(gBestParticle.points[j].y) + ") ");
		}
		results.write("\n");
		results.write(" ");
		results.close();
	}
}