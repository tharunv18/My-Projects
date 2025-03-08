import java.io.BufferedReader;
import java.util.HashMap; // import the HashMap class
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.*;
import java.lang.*;

public class SystemManager {
	private static ArrayList<Course> allPossibleCourses = new ArrayList<Course>();
	private static ArrayList<String[]> blockingRulesTogether = new ArrayList<String[]>();
	private static ArrayList<String[]> blockingRulesSeperate = new ArrayList<String[]>();
	private static ArrayList<String[]> blockingRulesTerms = new ArrayList<String[]>();
	private static ArrayList<String[]> sequenceRules = new ArrayList<String[]>();
	private static ArrayList<Student> students = new ArrayList<Student>();
	private static ArrayList<Course> allPossibleCoursesModify = new ArrayList<Course>();
	private static ArrayList<String> usedId = new ArrayList<String>();
	private static String takeOut;
	private static double bestEightOfEight = 0;

	public static void initializeBlockingRules() throws IOException {
		BufferedReader data = new BufferedReader(new FileReader("src/files/Course Blocking Rules.csv"));
		String[] blockRules = data.lines().toArray(String[]::new);
		for (int i = 5; i < blockRules.length; i++) {
			int start = 9;
			int end = 0;
			String[] line = blockRules[i].split(",");
			end = line[2].indexOf("in a Simultaneous blocking");
			if (end == -1) {
				end = line[2].indexOf("in a NotSimultaneous blocking");
				if (end != -1) {
					blockingRulesSeperate.add(line[2].substring(start, end).split(" "));
					// System.out.println("Apart: " + line[2].substring(start,end));
					continue;
				} else {
					end = line[2].indexOf("in a Terms blocking");
					if (end == -1) {
						continue;
					}
					blockingRulesTerms.add(line[2].substring(start, end).split(" "));
					// System.out.println("Terms: " + line[2].substring(start,end));
					continue;
				}
			}
			blockingRulesTogether.add(line[2].substring(start, end).split(" "));
			// System.out.println("Together: " + line[2].substring(start,end));
			// System.out.println("Rule: " + line[2]);
		}
//		System.out.println("\nSimulatnus: \n---------------------------------------------------" );
//		for(int j = 0; j < blockingRulesTogether.size(); j++) {
//			for (int k = 0; k < blockingRulesTogether.get(j).length; k++) {
//				int id = binStudentCourseSearch(0, allPossibleCourses.size() - 1, blockingRulesTogether.get(j)[k], allPossibleCourses);
//				if (id != -1) 
//					System.out.print(allPossibleCourses.get(id).getName()+ "   ");
//				else 
//					System.out.print(blockingRulesTogether.get(j)[k]+ "   ");
//			}
//			System.out.println();
//		}
//		System.out.println("\nApart: \n---------------------------------------------------" );
//		for(int j = 0; j < blockingRulesSeperate.size(); j++) {
//			for (int k = 0; k < blockingRulesSeperate.get(j).length; k++) {
//				int id = binStudentCourseSearch(0, allPossibleCourses.size() - 1, blockingRulesSeperate.get(j)[k], allPossibleCourses);
//				if (id != -1) 
//					System.out.print(allPossibleCourses.get(id).getName() + "   ");
//				else 
//					System.out.print(blockingRulesSeperate.get(j)[k]+ "   ");
//			}
//			System.out.println();
//		}
//		System.out.println("\nTerms: \n---------------------------------------------------" );
//		for(int j = 0; j < blockingRulesTerms.size(); j++) {
//			for (int k = 0; k < blockingRulesTerms.get(j).length; k++) {
//				int id = binStudentCourseSearch(0, allPossibleCourses.size() - 1, blockingRulesTerms.get(j)[k], allPossibleCourses);
//				if (id != -1) 
//					System.out.print(allPossibleCourses.get(id).getName()+ "   ");
//				else 
//					System.out.print(blockingRulesTerms.get(j)[k]+ "   ");
//			}
//			System.out.println();
//		}

		for (int j = 0; j < blockingRulesTogether.size(); j++) {
			Course newCourse = new Course();
			int totalEnroll = 0;
			for (int k = 0; k < blockingRulesTogether.get(j).length; k++) {
				newCourse.setId(newCourse.getId() + blockingRulesTogether.get(j)[k]);
				int index = binStudentCourseSearch(0, allPossibleCourses.size() - 1, blockingRulesTogether.get(j)[k],
						allPossibleCourses);
				if (index != -1) {
					if (k == 0) {
						newCourse.setName(allPossibleCourses.get(index).getName());
					} else {
						newCourse.setName(newCourse.getName() + " + " + allPossibleCourses.get(index).getName());
					}
					newCourse.setType(allPossibleCourses.get(index).getType());
					newCourse.setSections(newCourse.getSections() + allPossibleCourses.get(index).getSections());
					totalEnroll += allPossibleCourses.get(index).getMaxEnroll()
							* allPossibleCourses.get(index).getSections();
					allPossibleCourses.remove(index);
				} else {
					if (k == 0) {
						newCourse.setName(blockingRulesTogether.get(j)[k]);
					} else {
						newCourse.setName(newCourse.getName() + " + " + blockingRulesTogether.get(j)[k]);
					}
				} // else
			} // for
			String usedNums = "";
			if (newCourse.getSections() != 0) {
				newCourse.setMaxEnroll(totalEnroll / newCourse.getSections());
			}
			// System.out.println(newCourse.getSections() + " " + newCourse.getName());
			for (int k = 0; k < newCourse.getSections(); k++) {
				Course course = new Course();
				course.setId(newCourse.getId());
				course.setName(newCourse.getName());
				course.setType(newCourse.getType());
				course.setSections(newCourse.getSections());
				course.setMaxEnroll(newCourse.getMaxEnroll());
				if (usedNums.length() == 8) {
					usedNums = "";
				}
				usedNums += course.assignBlockAlphabetically(usedNums);
				allPossibleCourses.add(course);
			} // for
		} // for

		Course.initializeBlocks();
		// Course.printBlocks();

	}

	public static void initializeSequenceRules() throws IOException {
		BufferedReader data = new BufferedReader(new FileReader("src/files/Course Sequencing Rules.csv"));
		String[] seqRules = data.lines().toArray(String[]::new);
		for (int i = 5; i < seqRules.length; i++) {
			String[] line = seqRules[i].split(",");
			String ids[] = line[2].split(" before ");
			ids[0] = ids[0].substring(9);
			sequenceRules.add(ids);
			// System.out.println("ids first: " + ids[0] + " second " + ids[1] );
		}
//		System.out.println("Sequence Rules:\n---------------------------------------------------------");
//		for(int i = 0; i < sequenceRules.size();i ++) {
//			System.out.println(Arrays.deepToString(sequenceRules.get(i)));
//		}
	}

	public static void initializeCourses() throws IOException {
		BufferedReader data = new BufferedReader(new FileReader("src/files/Course Information (tally).csv"));
		String[] courseInfo = data.lines().toArray(String[]::new);
//		initializePriorityCourses();
		for (int i = 2; i < courseInfo.length; i += 2) {
			String[] line = courseInfo[i].split(",");
			String usedNums = "";
			boolean isFirstS = true;
			boolean isFirstF = true;
//			if (!usedId.contains(line[0])) {
			for (int j = 0; j < Integer.parseInt(line[8]); j++) {

				Course newCourse = new Course();
				newCourse.setId(line[0]);
				newCourse.setName(line[1]);
				newCourse.setType(line[2]);
				newCourse.setSections(Integer.parseInt(line[8]));
				newCourse.setMaxEnroll(Integer.parseInt(line[7]));

				if (newCourse.getId().contains("--L")) {
					if ((newCourse.getName().contains("LEADERSHIP")
							|| (newCourse.getName().contains("DANCE")
									&& String.valueOf(newCourse.getId().charAt(newCourse.getId().length() - 1))
											.equals("L"))
							|| (newCourse.getName().contains("ROBOTICS") && String
									.valueOf(newCourse.getId().charAt(newCourse.getId().length() - 1)).equals("L"))
							|| (newCourse.getName().contains("TUTORING") && String
									.valueOf(newCourse.getId().charAt(newCourse.getId().length() - 1)).equals("L"))
							|| (newCourse.getName().contains("SCHOLARSHIP")
									&& String.valueOf(newCourse.getId().charAt(newCourse.getId().length() - 1))
											.equals("L"))
							|| (newCourse.getName().contains("GRAPHIC PRODUCTION") && String
									.valueOf(newCourse.getId().charAt(newCourse.getId().length() - 1)).equals("L"))
							|| (newCourse.getName().contains("CHOIR") && String
									.valueOf(newCourse.getId().charAt(newCourse.getId().length() - 1)).equals("L"))
							|| newCourse.getName().equals("GEOMETRY 12") || newCourse.getName().contains("BAND")
							|| newCourse.getName().contains("ORCHESTRA"))) {
						newCourse.addLinearCourse();
						newCourse.setBlock(9);
					} else {
						usedNums += newCourse.assignBlockFirst(usedNums);
						allPossibleCourses.add(newCourse);

						Course linear = new Course();

						linear.setId(newCourse.getId());
						linear.setName(newCourse.getName());
						linear.setType(newCourse.getType());
						linear.setSections(newCourse.getSections());
						linear.setMaxEnroll(newCourse.getMaxEnroll());
						usedNums += linear.assignBlockSecond(usedNums);

						allPossibleCourses.add(linear);
					}
					continue;

				}

				if (usedNums.length() == 8) {
					usedNums = "";
				}
				// if (j == 0) {
				boolean secondSemester = false;
				boolean firstSemester = false;
				for (int y = 0; y < sequenceRules.size(); y++) {
					if (sequenceRules.get(y)[1].contains(newCourse.getId())) {
						// usedNums = "";
						// System.out.println(newCourse.getName() + ": is second");
						secondSemester = true;
					}
				}
				for (int y = 0; y < sequenceRules.size(); y++) {
					if (sequenceRules.get(y)[0].contains(newCourse.getId())) {

						firstSemester = true;
					}
				}

				if (firstSemester && isFirstF) {
					usedNums += newCourse.assignBlock("5678");
					isFirstF = false;
				} else if (secondSemester && isFirstS) {
					// System.out.println(newCourse.getName());
					usedNums += newCourse.assignBlock("1234");
					isFirstS = false;
				} else {
					usedNums += newCourse.assignBlock(usedNums);
				}

//					allPossibleCourses.add(newCourse);

				// } else {
//					usedNums += newCourse.assignBlock(usedNums);
				allPossibleCourses.add(newCourse);
				// }
			} // for

//			} // if
		}
		Course.initializeBlocks();
//		Course.printBlocks();
	}

	public static void initializeStudentRequests() throws IOException {
		Student currentStudent = new Student();
		BufferedReader data = new BufferedReader(new FileReader("src/files/cleanedstudentrequests.csv"));
		String[] studentRequests = data.lines().toArray(String[]::new);
		students = new ArrayList<Student>();
		for (int i = 0; i < studentRequests.length; i++) {
			String[] line = studentRequests[i].split(",");
			if (line[0].equals("ID")) {
				if (Integer.parseInt(line[1]) != 1000) {
					currentStudent.getPrimaryCourses().sort(null);
					currentStudent.getAlternateCourses().sort(null);
					students.add(currentStudent);
				}
				currentStudent = new Student(Integer.parseInt(line[1]));
				i++;
			} else {
				int courseIndex = linearStudentCourseSearch(line[0], allPossibleCourses);
				if (courseIndex == -1) { // if course does not exist in course list, generate a new course based on
					Course nullCourse = new Course();
					nullCourse.setId(line[0]);
					nullCourse.setName(line[2]);
					nullCourse.setType("null");
					nullCourse.setMaxEnroll(0);
					nullCourse.setSections(0);
					if (line[3].equals("Y")) {
						currentStudent.addAlternateCourses(nullCourse);
					} else {
						currentStudent.addPrimaryCourses(nullCourse);
					} // else
					allPossibleCourses.add(nullCourse);
				} else {
					if (line[3].equals("Y")) {
						currentStudent.addAlternateCourses(allPossibleCourses.get(courseIndex));
					} else {
						currentStudent.addPrimaryCourses(allPossibleCourses.get(courseIndex));
					} // else
				} // else
			} // else
				// System.out.println("Student Added: " + currentStudent.getId());
		} // for
		students.add(currentStudent);
	} // initializeStudentRequests

//	public static void initializeTimeTables() throws IOException {
//
//		for (int i = 0; i < students.size(); i++) {
//			givePriorityCourses(i);
//			for (int j = 0; j < students.get(i).getPrimaryCourses().size(); j++) {
//				Course currentCourse = students.get(i).getPrimaryCourses().get(j);
//				if (currentCourse.getName().contains("CHALLENGE") || currentCourse.getName().contains("AP ") || currentCourse.getId().contains("--L")) {
//					continue;
//				}
////				if (currentCourse.getName().contains("Linear") || currentCourse.getName().contains("LINEAR")
////						|| currentCourse.getName().contains("GEOMETRY")) {
////					String currentId = currentCourse.getId();
////					ArrayList<Course> blockList = Course.getBlocks().get(8).getCourses();
////					int currentIndex = linearStudentCourseSearch(currentId, blockList);
////					if (currentIndex != -1) {
////						if (blockList.get(currentIndex).getMaxEnroll() > 0) {
////							if (!students.get(i).hasCourse(blockList.get(currentIndex).getName())) {
////								students.get(i).addCourseToTimetable(blockList.get(currentIndex));
////								blockList.get(currentIndex).decrementMaxEnroll();
////								blockList.get(currentIndex).incrementEnrolled();
////								blockList.get(currentIndex).addStudent(students.get(i));
//////								if (i < 100) {
//////									System.out.println("student " + i + " was assigned linear course "
//////											+ blockList.get(currentIndex).getName() + " in block "
//////											+ blockList.get(currentIndex).getBlock() + " with "
//////											+ blockList.get(currentIndex).getMaxEnroll() + " spots left");
//////								}
////							}
////						}
////					}
//				//} else {
//					int k;
//					String usedNums = "";
//					do {
//						k = (int) (Math.random() * 8);
//						if (usedNums.contains(String.valueOf(k))) {
//							continue;
//						} else {
//							usedNums += k;
//						} // else
//						int counter = 0;
//						String currentId = currentCourse.getId();
//						ArrayList<Course> blockList = Course.getBlocks().get(k).getCourses();
//						int currentIndex = linearStudentCourseSearch(currentId, blockList);
//						if (currentIndex != -1) {
//							counter++;
//							if (blockList.get(currentIndex).getMaxEnroll() > 0) {
//								if (!students.get(i).isFilled(k)) {
//									blockList.get(currentIndex).decrementMaxEnroll();
//									blockList.get(currentIndex).incrementEnrolled();
//									blockList.get(currentIndex).addStudent(students.get(i));
//									blockList.get(currentIndex).setBlock(k + 1);
//									students.get(i).addCourseToTimetable(blockList.get(currentIndex));
//									students.get(i).fillBlock(k);
////									if (i < 100) {
////										System.out.println("student " + i + " was assigned primary course "
////												+ blockList.get(currentIndex).getName() + " in block "
////												+ blockList.get(currentIndex).getBlock() + " with "
////												+ blockList.get(currentIndex).getMaxEnroll() + " spots left");
////									}
//									break;
//								} // if
//							} else if (counter == currentCourse.getSections()) {
//								for (int l = 0; l < students.get(i).getAlternateCourses().size(); l++) {
//									currentCourse = students.get(i).getAlternateCourses().get(l);
//									int m;
//									usedNums = "";
//									do {
//										m = (int) (Math.random() * 8);
//										if (usedNums.contains(String.valueOf(m))) {
//											continue;
//										} else {
//											usedNums += m;
//										} // else
//										counter = 0;
//										currentId = currentCourse.getId();
//										blockList = Course.getBlocks().get(m).getCourses();
//										currentIndex = linearStudentCourseSearch(currentId, blockList);
//										if (currentIndex != -1) {
//											counter++;
//											if (blockList.get(currentIndex).getMaxEnroll() > 0) {
//												if (!students.get(i).isFilled(m)) {
//													blockList.get(currentIndex).setBlock(m + 1);
//													blockList.get(currentIndex).decrementMaxEnroll();
//													blockList.get(currentIndex).incrementEnrolled();
//													blockList.get(currentIndex).addStudent(students.get(i));
//													students.get(i).addCourseToTimetable(blockList.get(currentIndex));
//													students.get(i).fillBlock(m);
////													if (i > 100) {
////														System.out.println("student " + i
////																+ " was assigned alternate course "
////																+ blockList.get(currentIndex).getName() + " in block "
////																+ blockList.get(currentIndex).getBlock() + " with "
////																+ blockList.get(currentIndex).getMaxEnroll()
////																+ " spots left");
////													}
//													break;
//												} // if
//											} // if
//										} // if
//									} while (usedNums.length() < 8);
//								} // for
//							} // if
//						} // if
//					} while (usedNums.length() < 8);
//				//} // else
//			} // for
//				// to print specific student's timetable
//			if (students.get(i).getId() <= 1100) {
//				students.get(i).printTimetable();
//			} // if
//		} // for
//
//		boolean changeTimetable = false;
//		int courseToRemove = 0;
//		for (int i = 0; i < Course.getBlocks().size(); i++) {
//			for (int j = 0; j < Course.getBlocks().get(i).getCourses().size(); j++) {
//				if ((double) (Course.getBlocks().get(i).getCourses().get(j).getEnrolled())
//						/ (Course.getBlocks().get(i).getCourses().get(j).getEnrolled()
//								+ Course.getBlocks().get(i).getCourses().get(j).getMaxEnroll()) < 0.5) {
//					courseToRemove = j;
//					takeOut += Course.getBlocks().get(i).getCourses().get(j).getName();
//					for (int k = 0; k < Course.getBlocks().get(i).getCourses().get(j).getStudents().size(); k++) {
//						Course.getBlocks().get(i).getCourses().get(j).getStudents().get(k)
//								.removeCourseFromTimeTable(Course.getBlocks().get(i).getCourses().get(j).getName());
//					}
//					Course.getBlocks().get(i).removeCourse(courseToRemove);
//					j--;
//					changeTimetable = true;
//				}
//			}
//		}
//
//		if (changeTimetable) {
////			students = new ArrayList();
////			initializeStudentRequests();
////			allPossibleCourses = new ArrayList();
////			initializeCourses();
//			initializeTimeTables();
//		}
//	} // initializeTimeTables

	public static void initializeTimeTables() {
		ArrayList<Integer> studentIds = new ArrayList<Integer>();
		for (int i = 0; i < students.size(); i++) {
			studentIds.add(i);
		}
		for (int i = 0; i < students.size(); i++) {
			ArrayList<Course> cleanedPrimaryCourses = (ArrayList<Course>) students.get(i).getPrimaryCourses().clone();
			for (int j = 0; j < cleanedPrimaryCourses.size(); j++) {
				if (i == 178) {
					System.out.println(
							cleanedPrimaryCourses.get(j).getName() + " " + cleanedPrimaryCourses.get(j).getMaxEnroll());
				}
				if ((cleanedPrimaryCourses.get(j).getName().contains("LEADERSHIP")
						|| (cleanedPrimaryCourses.get(j).getName().contains("DANCE")
								&& String
										.valueOf(cleanedPrimaryCourses
												.get(j).getId().charAt(cleanedPrimaryCourses.get(j).getId().length()
														- 1))
										.equals("L"))
						|| (cleanedPrimaryCourses.get(j).getName().contains("ROBOTICS")
								&& String.valueOf(cleanedPrimaryCourses.get(j).getId()
										.charAt(cleanedPrimaryCourses.get(j).getId().length() - 1)).equals("L"))
						|| (cleanedPrimaryCourses.get(j).getName().contains("TUTORING")
								&& String
										.valueOf(cleanedPrimaryCourses
												.get(j).getId().charAt(cleanedPrimaryCourses.get(j).getId().length()
														- 1))
										.equals("L"))
						|| (cleanedPrimaryCourses.get(j).getName().contains("SCHOLARSHIP")
								&& String.valueOf(cleanedPrimaryCourses.get(j).getId()
										.charAt(cleanedPrimaryCourses.get(j).getId().length() - 1)).equals("L"))
						|| (cleanedPrimaryCourses.get(j).getName().contains("GRAPHIC PRODUCTION")
								&& String
										.valueOf(cleanedPrimaryCourses
												.get(j).getId().charAt(cleanedPrimaryCourses.get(j).getId().length()
														- 1))
										.equals("L"))
						|| (cleanedPrimaryCourses.get(j).getName().contains("CHOIR")
								&& String.valueOf(cleanedPrimaryCourses.get(j).getId()
										.charAt(cleanedPrimaryCourses.get(j).getId().length() - 1)).equals("L"))
						|| cleanedPrimaryCourses.get(j).getName().equals("GEOMETRY 12")
						|| cleanedPrimaryCourses.get(j).getName().contains("BAND")
						|| cleanedPrimaryCourses.get(j).getName().contains("ORCHESTRA"))) {
					if (cleanedPrimaryCourses.get(j).getMaxEnroll() > 0) {
						cleanedPrimaryCourses.get(j).setBlock(9);
						cleanedPrimaryCourses.get(j).decrementMaxEnroll();
						cleanedPrimaryCourses.get(j).incrementEnrolled();
						cleanedPrimaryCourses.get(j).addStudent(students.get(i));
						students.get(i).addCourseToTimetable(cleanedPrimaryCourses.get(j));
						cleanedPrimaryCourses.remove(j);
						j--;
					} else {
						cleanedPrimaryCourses.remove(j);
						j--;
					}
				} // if
			} // for
//				while (cleanedPrimaryCourses.size() > 8) {
//					cleanedPrimaryCourses.remove(cleanedPrimaryCourses.size() - 1);
//				}
			ArrayList<ArrayList<Course>> allCoursePermutations = generateAllPermutations(cleanedPrimaryCourses);

			// maximum-finding the best permutation
			ArrayList<Course> bestSchedule = new ArrayList<Course>();
			int bestScore = -1;
			for (int j = 0; j < allCoursePermutations.size(); j++) {
				int currentScore = 0;
				for (int k = 0; k < allCoursePermutations.get(j).size(); k++) {
					ArrayList<Course> blockList = Course.getBlocks().get(k).getCourses();
					int index = linearStudentCourseSearch(allCoursePermutations.get(j).get(k).getId(), blockList);
					if (index != -1) {
						if (blockList.get(index).getMaxEnroll() > 0) {
							if (!students.get(i).isFilled(k)) {
								currentScore++;
							} // if
						} // if
					} // if
				} // for
				if (currentScore > bestScore) {
					bestScore = currentScore;
					bestSchedule = (ArrayList<Course>) allCoursePermutations.get(j).clone();
				} // if
			} // for

			// add to timetable
			for (int j = 0; j < bestSchedule.size(); j++) {
				ArrayList<Course> blockList = Course.getBlocks().get(j).getCourses();
				int index = linearStudentCourseSearch(bestSchedule.get(j).getId(), blockList);
				if (index != -1) {
					if (blockList.get(index).getMaxEnroll() > 0) {
						if (!students.get(i).isFilled(j)) {
							blockList.get(index).setBlock(j + 1);
							blockList.get(index).decrementMaxEnroll();
							blockList.get(index).incrementEnrolled();
							blockList.get(index).addStudent(students.get(i));
							students.get(i).addCourseToTimetable(blockList.get(index));
							students.get(i).fillBlock(j);
						} // if
					} // if
				} // if
			} // for

			if (i == 178) {
				students.get(i).printTimetable();
			}
//			} // if
		} //

		boolean changeTimetable = false;
		int courseToRemove = 0;
		for (int i = 0; i < Course.getBlocks().size(); i++) {
			for (int j = 0; j < Course.getBlocks().get(i).getCourses().size(); j++) {
				if ((double) (Course.getBlocks().get(i).getCourses().get(j).getEnrolled())
						/ (Course.getBlocks().get(i).getCourses().get(j).getEnrolled()
								+ Course.getBlocks().get(i).getCourses().get(j).getMaxEnroll()) < 0.5) {
					courseToRemove = j;
					takeOut += Course.getBlocks().get(i).getCourses().get(j).getName();
					for (int k = 0; k < Course.getBlocks().get(i).getCourses().get(j).getStudents().size(); k++) {
						Course.getBlocks().get(i).getCourses().get(j).getStudents().get(k)
								.removeCourseFromTimeTable(Course.getBlocks().get(i).getCourses().get(j).getName());
					} // for
					Course.getBlocks().get(i).removeCourse(courseToRemove);
					j--;
					changeTimetable = true;
				} // if
			} // for
		} // for

		if (changeTimetable) {
			initializeTimeTables();
		} // if
	} // initializeTimeTables

	// from geeks for geeks (modified for arraylists)
	public static ArrayList<ArrayList<Course>> generateAllPermutations(ArrayList<Course> list) {
		if (list.size() == 0) {
			ArrayList<ArrayList<Course>> empty = new ArrayList<ArrayList<Course>>();
			empty.add(new ArrayList<Course>());
			return empty;
		} // if

		Course course = new Course();
		course.setId(list.get(0).getId());
		course.setName(list.get(0).getName());
		course.setType(list.get(0).getType());
		course.setSections(list.get(0).getSections());
		course.setMaxEnroll(list.get(0).getMaxEnroll());

		ArrayList<Course> newList = (ArrayList<Course>) list.clone();
		newList.remove(0);

		ArrayList<ArrayList<Course>> prevResult = generateAllPermutations(newList);
		ArrayList<ArrayList<Course>> result = new ArrayList<ArrayList<Course>>();

		for (ArrayList<Course> a : prevResult) {
			for (int i = 0; i <= a.size(); i++) {
				ArrayList<Course> current = new ArrayList<Course>();
				for (int j = 0; j < i; j++) {
					current.add(a.get(j));
				} // for
				current.add(course);
				for (int j = i; j < a.size(); j++) {
					current.add(a.get(j));
				} // for
				result.add(current);
			} // for
		} // for

		return result;
	} // generateAllPermutations

	public void writeOverlapsToFile() {
		HashMap<String, Integer> overlappedCourses = new HashMap<String, Integer>();

		for (Student student : students) {
			for (int i = 0; i < student.getPrimaryCourses().size(); i++) {
				for (int j = i + 1; j < student.getPrimaryCourses().size(); j++) {
					String courses = student.getPrimaryCourses().get(i).getId() + " "
							+ student.getPrimaryCourses().get(j).getId();
					if (overlappedCourses.containsKey(courses)) {
						overlappedCourses.put(courses, overlappedCourses.get(courses) + 1);
					} else {
						overlappedCourses.put(courses, 1);
					}
				}
			}
		}
		HashMap<String, Integer> sortedOverlappedCourses = sortByValue(overlappedCourses);
		writeHashMapToFile("overlaps.txt", sortedOverlappedCourses);
	}

	// function to sort hashmap by values
	public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public static void givePriorityCourses(int num) {
		boolean APStudent = false;
		boolean challengeStudent = false;
		boolean isLinear = false;

		for (Course course : students.get(num).getPrimaryCourses()) {
			if (course.getName().contains("AP ")) {
				APStudent = true;
			}
			if (course.getName().contains("CHALLENGE")) {
				challengeStudent = true;
			}
			if (course.getId().contains("--L")) {
				isLinear = true;
			}

		}

		if (isLinear) {
			for (int n = 0; n < students.get(num).getPrimaryCourses().size(); n++) {
				Course course = students.get(num).getPrimaryCourses().get(n);
				if (course.getId().contains("--L") && !course.getName().contains("LEADERSHIP")
						&& !course.getName().contains("GEOMETRY 12") && !course.getName().contains("GRAPHIC PRODUCTION")
						&& !course.getName().contains("BAND") && !course.getName().contains("DANCE")
						&& !course.getName().contains("ROBOTICS") && !course.getName().contains("TUTORING")
						&& !course.getName().contains("SCHOLARSHIP") && !course.getName().contains("CHOIR")) {

					// System.out.println(course.getName());

					for (Course courseOne : allPossibleCourses) {
						if (courseOne.getName().equals(course.getName()) && courseOne.getMaxEnroll() > 0
								&& !(students.get(num).isFilled(courseOne.getBlock() - 1))) {
							// System.out.println(courseOne.getName() + " " + courseOne.getMaxEnroll() + " "
							// + !(students.get(num).isFilled(courseOne.getBlock() - 1)));
							students.get(num).addCourseToTimetable(courseOne);
							students.get(num).fillBlock(courseOne.getBlock() - 1);

							for (int i = 0; i < Course.getBlocks().size(); i++) {
								for (int j = 0; j < Course.getBlocks().get(i).getCourses().size(); j++) {
									if (Course.getBlocks().get(i).getCourses().get(j).getName()
											.equals(course.getName())) {
										// System.out.println();
										Course.getBlocks().get(i).getCourses().get(j).decrementMaxEnroll();
										Course.getBlocks().get(i).getCourses().get(j).incrementEnrolled();
										Course.getBlocks().get(i).getCourses().get(j).addStudent(students.get(num));

									}
								}
							}
						}

					}
				}
			}

		}

		if (APStudent) {
			for (int n = 0; n < students.get(num).getPrimaryCourses().size(); n++) {
				Course course = students.get(num).getPrimaryCourses().get(n);
				if (course.getName().contains("AP ") && course.getMaxEnroll() > 0
						&& !(students.get(num).isFilled(course.getBlock() - 1))) {
					students.get(num).addCourseToTimetable(course);
					students.get(num).fillBlock(course.getBlock() - 1);
					for (int i = 0; i < Course.getBlocks().size(); i++) {
						for (int j = 0; j < Course.getBlocks().get(i).getCourses().size(); j++) {
							if (Course.getBlocks().get(i).getCourses().get(j).getName().equals(course.getName())) {
								Course.getBlocks().get(i).getCourses().get(j).decrementMaxEnroll();
								Course.getBlocks().get(i).getCourses().get(j).incrementEnrolled();
								Course.getBlocks().get(i).getCourses().get(j).addStudent(students.get(num));
							}
						}
					}
				}
			}
		}
		if (challengeStudent) {
			for (int n = 0; n < students.get(num).getPrimaryCourses().size(); n++) {
				Course course = students.get(num).getPrimaryCourses().get(n);
				if (course.getName().contains("CHALLENGE") && course.getMaxEnroll() > 0
						&& !(students.get(num).isFilled(course.getBlock() - 1))) {
					students.get(num).addCourseToTimetable(course);
					students.get(num).fillBlock(course.getBlock() - 1);
					for (int i = 0; i < Course.getBlocks().size(); i++) {
						for (int j = 0; j < Course.getBlocks().get(i).getCourses().size(); j++) {
							if (Course.getBlocks().get(i).getCourses().get(j).getName().equals(course.getName())) {
								Course.getBlocks().get(i).getCourses().get(j).decrementMaxEnroll();
								Course.getBlocks().get(i).getCourses().get(j).incrementEnrolled();
								Course.getBlocks().get(i).getCourses().get(j).addStudent(students.get(num));
							}
						}
					}
				}
			}
		}
	}

	public static void getMetrics() {
		int applicable = students.size();
		int right = 0;
		int full = 0;
		int fullNA = 0;
		int close = 0;
		int kindaclose = 0;
		int closeNA = 0;
		int closeNA2 = 0;
		int overall = 0;
		int overallR = 0;
		int twoWrong = 0;
		int threeToEight = 0;
		int altUsed = 0;
		int primeUsed = 0;
		int outsideTimeTable = 0;
		int outsideTimeTableGot = 0;
		
		for (Student student : students) {
			student.getTimetable().sort(null);
			
			
			
			// System.out.println(student.getPrimaryCourses().size() -
			// student.getTimetable().size());
			// if(student.getPrimaryCourses().size() - student.getTimetable().size() ==1 ||
			// student.getPrimaryCourses().size() - student.getTimetable().size() ==2 ) {
			//
			// twoWrong++;
			// }
			// System.out.println(student.getPrimaryCourses().size() -
			// student.getTimetable().size() >2);
			// if(student.getPrimaryCourses().size() - student.getTimetable().size() >2 ) {
			//
			// threeToEight++;
			// }
			if (student.getPrimaryCourses().size() < 8) {
				applicable--;
				right = 0;
				continue;
			} // if
			for (int i = 0; i < student.getPrimaryCourses().size(); i++) {
				if ((student.getPrimaryCourses().get(i).getName().contains("LEADERSHIP")
						|| (student.getPrimaryCourses().get(i).getName().contains("DANCE")
								&& String.valueOf(student.getPrimaryCourses().get(i).getId().charAt(student.getPrimaryCourses().get(i).getId().length() - 1))
										.equals("L"))
						|| (student.getPrimaryCourses().get(i).getName().contains("ROBOTICS") && String
								.valueOf(student.getPrimaryCourses().get(i).getId().charAt(student.getPrimaryCourses().get(i).getId().length() - 1)).equals("L"))
						|| (student.getPrimaryCourses().get(i).getName().contains("TUTORING") && String
								.valueOf(student.getPrimaryCourses().get(i).getId().charAt(student.getPrimaryCourses().get(i).getId().length() - 1)).equals("L"))
						|| (student.getPrimaryCourses().get(i).getName().contains("SCHOLARSHIP")
								&& String.valueOf(student.getPrimaryCourses().get(i).getId().charAt(student.getPrimaryCourses().get(i).getId().length() - 1))
										.equals("L"))
						|| (student.getPrimaryCourses().get(i).getName().contains("GRAPHIC PRODUCTION") && String
								.valueOf(student.getPrimaryCourses().get(i).getId().charAt(student.getPrimaryCourses().get(i).getId().length() - 1)).equals("L"))
						|| (student.getPrimaryCourses().get(i).getName().contains("CHOIR") && String
								.valueOf(student.getPrimaryCourses().get(i).getId().charAt(student.getPrimaryCourses().get(i).getId().length() - 1)).equals("L"))
						|| student.getPrimaryCourses().get(i).getName().equals("GEOMETRY 12") || student.getPrimaryCourses().get(i).getName().contains("BAND")
						|| student.getPrimaryCourses().get(i).getName().contains("ORCHESTRA"))) {
					outsideTimeTable++;
				}
				
				
				
				
				for (int j = 0; j < student.getTimetable().size(); j++) {
					
					
					if ((student.getTimetable().get(j).getName().contains("LEADERSHIP")
							|| (student.getTimetable().get(j).getName().contains("DANCE")
									&& String.valueOf(student.getTimetable().get(j).getId().charAt(student.getTimetable().get(j).getId().length() - 1))
											.equals("L"))
							|| (student.getTimetable().get(j).getName().contains("ROBOTICS") && String
									.valueOf(student.getTimetable().get(j).getId().charAt(student.getTimetable().get(j).getId().length() - 1)).equals("L"))
							|| (student.getTimetable().get(j).getName().contains("TUTORING") && String
									.valueOf(student.getTimetable().get(j).getId().charAt(student.getTimetable().get(j).getId().length() - 1)).equals("L"))
							|| (student.getTimetable().get(j).getName().contains("SCHOLARSHIP")
									&& String.valueOf(student.getTimetable().get(j).getId().charAt(student.getTimetable().get(j).getId().length() - 1))
											.equals("L"))
							|| (student.getTimetable().get(j).getName().contains("GRAPHIC PRODUCTION") && String
									.valueOf(student.getTimetable().get(j).getId().charAt(student.getTimetable().get(j).getId().length() - 1)).equals("L"))
							|| (student.getTimetable().get(j).getName().contains("CHOIR") && String
									.valueOf(student.getTimetable().get(j).getId().charAt(student.getTimetable().get(j).getId().length() - 1)).equals("L"))
							|| student.getTimetable().get(j).getName().equals("GEOMETRY 12") || student.getTimetable().get(j).getName().contains("BAND")
							|| student.getTimetable().get(j).getName().contains("ORCHESTRA"))) {
						outsideTimeTableGot++;
					}
					
					
					if (student.getTimetable().get(j).getId().contains(student.getPrimaryCourses().get(i).getId())) {
						right++;
						primeUsed++;
						if (student.getTimetable().get(j).getId().contains("--L")) {
							// System.out.println(student.getTimetable().get(j).getName());
							right++;
							primeUsed++;
							overallR++;
						}
						overallR++;
					} // if
				} // for
				overall++;
			} // for

			if (right >= 8) {
				closeNA++;
				fullNA++;
				closeNA2++;
			} else if (right >= 7) {
				closeNA2++;
				closeNA++;
			} else if (right >= 6) {
				closeNA2++;
			}
			for (int i = 0; i < student.getAlternateCourses().size(); i++) {
				for (int j = 0; j < student.getTimetable().size(); j++) {
					if (student.getTimetable().get(j).getId().contains(student.getAlternateCourses().get(i).getId())) {
						right++;
						altUsed++;
						if (student.getTimetable().get(j).getId().contains("--L")) {
							// System.out.println(student.getTimetable().get(j).getName());
							right++;
							overallR++;
							altUsed++;
						}
						overallR++;
					} // if
				} // for
			} // for
			if (right >= 8) {
				kindaclose++;
				close++;
				full++;
			} else if (right >= 7) {
				close++;
				kindaclose++;
			} else if (right >= 6) {
				kindaclose++;
			}
			if ((student.getPrimaryCourses().size() - outsideTimeTableGot)- (student.getTimetable().size() - outsideTimeTableGot) == 1 ||(student.getPrimaryCourses().size() - outsideTimeTableGot)- (student.getTimetable().size() - outsideTimeTableGot) == 2) {

				twoWrong++;
			}
			if ((student.getPrimaryCourses().size() - outsideTimeTableGot)- (student.getTimetable().size() - outsideTimeTableGot) > 2) {

				threeToEight++;
			}
			right = 0;
		} // for

		System.out.println("Number of Courses in each block: \nS1: A:" + Course.getBlocks().get(0).getCourses().size()
				+ " B:" + Course.getBlocks().get(1).getCourses().size() + " C:"
				+ Course.getBlocks().get(2).getCourses().size() + " D:"
				+ Course.getBlocks().get(3).getCourses().size());
		System.out.println("S2: A:" + Course.getBlocks().get(4).getCourses().size() + " B:"
				+ Course.getBlocks().get(5).getCourses().size() + " C:" + Course.getBlocks().get(6).getCourses().size()
				+ " D:" + Course.getBlocks().get(7).getCourses().size());
		System.out.println();

		System.out.println("The overall percent of requested courses that were placed: "
				+ (double) overallR / overall * 100 + "%");
		System.out.println("The number of requested courses:" + overall);
		System.out.println("The number of courses placed:" + overallR);
		System.out.println();

		System.out.println(
				"Percent of students with 8/8 of requested courses: " + (double) fullNA / applicable * 100 + "%");
		System.out.println("Percent of students with 8/8 courses (requested or alternate): "
				+ (double) full / applicable * 100 + "%");
		System.out.println();

		// System.out.println(twoWrong);
		System.out.println("Percent of students with 1-2 courses not fulfilled (alt or requested):"
				+ (double) twoWrong / students.size() * 100 + "%");
		System.out.println("Percent of students with 3-8 courses not fulfilled (alt or requested): "
				+ (double) threeToEight / students.size() * 100 + "%");
		System.out.println();
		
		
		System.out.println("Percent of Students who got 8/8 or 7/8 of their requested courses including alternates: "
				+ (double) close / applicable * 100 + "%");
		System.out.println(
				"Percent of Students who got 8/8 or 7/8 or 6/8 of their requested courses including alternates: "
						+ (double) kindaclose / applicable * 100 + "%");
		System.out.println("Percent of Students who got 8/8 of the courses they requested not including alternates: "
				+ (double) fullNA / applicable * 100 + "%");
		System.out
				.println("Percent of Students who got 8/8 or 7/8 of their requested courses not including alternates: "
						+ (double) closeNA / applicable * 100 + "%");
		System.out.println(
				"Percent of Students who got 8/8 or 7/8 or 6/8 of their requested courses not including alternates: "
						+ (double) closeNA2 / applicable * 100 + "%");
		Course.getLines().add("The overall percent of requested courses that were placed: "
				+ (double) overallR / overall * 100 + "%");
		Course.getLines().add("Percent of Students who got 8/8 of the courses they requested including alternates: "
				+ (double) full / applicable * 100 + "%");
		Course.getLines().add("Percent of Students who got 8/8 or 7/8 of their requested courses including alternates: "
				+ (double) close / applicable * 100 + "%");
		Course.getLines().add("Percent of Students who got 8/8 of the courses they requested not including alternates: "
				+ (double) fullNA / applicable * 100 + "%");
		Course.getLines()
				.add("Percent of Students who got 8/8 or 7/8 of their requested courses not including alternates: "
						+ (double) closeNA / applicable * 100 + "%");
		Course.getLines().add("Number of Courses in each block: \nS1: A:" + Course.getBlocks().get(0).getCourses().size()
				+ " B:" + Course.getBlocks().get(1).getCourses().size() + " C:"
				+ Course.getBlocks().get(2).getCourses().size() + " D:"
				+ Course.getBlocks().get(3).getCourses().size());
		Course.getLines().add("S2: A:" + Course.getBlocks().get(4).getCourses().size() + " B:"
				+ Course.getBlocks().get(5).getCourses().size() + " C:" + Course.getBlocks().get(6).getCourses().size()
				+ " D:" + Course.getBlocks().get(7).getCourses().size());
		Course.getLines().add("The number of requested courses:" + overall);
		Course.getLines().add("The number of courses placed:" + overallR);
		Course.getLines()
				.add("Percent of Students who got 8/8 or 7/8 of their requested courses not including alternates: "
						+ (double) closeNA / applicable * 100 + "%");
		Course.getLines().add("The overall percent of requested courses that were placed: "
				+ (double) overallR / overall * 100 + "%");
		Course.getLines().add("Percent of Students who got 8/8 of the courses they requested including alternates: "
				+ (double) full / applicable * 100 + "%");
	} // getMetrics

	private static int binCourseSearch(int l, int r, String id) {
		if (r >= l) {
			int m = l + (r - l) / 2;
			if (allPossibleCourses.get(m).getId().contains(id)) {
				return m;
			}
			if (allPossibleCourses.get(m).getId().compareTo(id) > 0) {
				return binCourseSearch(l, m - 1, id);
			}
			return binCourseSearch(m + 1, r, id);
		}
		return -1;
	}

	private static int binStudentCourseSearch(int l, int r, String id, ArrayList<Course> list) {
		if (r >= l) {
			int m = l + (r - l) / 2;
			if (list.get(m).getId().contains(id)) {
				return m;
			}
			if (list.get(m).getId().compareTo(id) > 0) {
				return binStudentCourseSearch(l, m - 1, id, list);
			}
			return binStudentCourseSearch(m + 1, r, id, list);
		}
		return -1;
	}

	private static int linearStudentCourseSearch(String id, ArrayList<Course> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getId().contains(id)) {
				return i;
			}
		}
		return -1;
	}

//  reads fileName and returns the contents as String array
	// with each line of the file as an element of the array
	public static HashMap<String, Integer> getFileContents(String fileName) {
		HashMap<String, Integer> content = new HashMap<String, Integer>();
		try {
			// Read the lines from the file and collect them into a list
			List<String> lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList());

			// copy the lines from the list into a 1D array
			// content = lines.toArray(new String[0]);
			for (int i = 0; i < lines.size(); i++) {
				String[] sections = lines.get(i).split(": ");
				content.put(sections[0], Integer.parseInt(sections[1]));
			}

		} catch (IOException e) {
			System.out.println("File Read Error");
			e.printStackTrace();
		}

		return content;

	} // getFileContents

	// writes the array a to fileName, one array element per line in the file
	public static void writeHashMapToFile(String fileName, HashMap<String, Integer> a) {

		// this is called a try-with-resources
		// it automatically closes the resource when
		// the program is done with it
		try (FileWriter writer = new FileWriter(fileName)) {

			// Write each line followed by a newline character
			for (Map.Entry<String, Integer> set : a.entrySet()) {
				if (set.getValue() > 10) {
					writer.write(set.getKey() + ": " + set.getValue() + "\n");
				}
			}
		} catch (IOException e) {
			System.out.println("File Write Error");
			e.printStackTrace();
		}
	} // writeArrayToFile

}