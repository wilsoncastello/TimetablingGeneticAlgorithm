package br.edu.ifsc.TimetablingGeneticAlgorithm.util;

import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Teacher;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Course;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Lesson;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.Room;
import br.edu.ifsc.TimetablingGeneticAlgorithm.domain.itc.UnavailabilityConstraint;

import java.util.ArrayList;
import java.util.List;


public class ConvertFactory {

    public static DTOITC convertIFSCtoITC(DTOIFSC dtoifsc) throws ClassNotFoundException {
        DTOITC dtoitc = new DTOITC();

        //ROOMS
        int roomSize = dtoifsc.getRooms().size();
        Room[] room = new Room[roomSize];
        for (int i = 0; i < roomSize; i++) {
            room[i] = new Room();
            room[i].setRoomId(String.valueOf(dtoifsc.getRooms().get(i).getRoomId()));
            room[i].setCapacity(Integer.MAX_VALUE);
        }
        dtoitc.setRooms(room);

        //LESSONS


        int lessonSize = dtoifsc.getSubjects().size();
        Lesson[] lessons = new Lesson[lessonSize];
        for (int i = 0; i < lessonSize; i++) {
            lessons[i] = new Lesson();
            int lessonId = dtoifsc.getSubjects().get(i).getId();
            lessons[i].setCourseId(retriveLessonsCourse(lessonId, dtoifsc.getLessons()));
            lessons[i].setLessonId(String.valueOf(lessonId));
            lessons[i].setProfessorId(retrieveProfessorsId(lessonId, dtoifsc.getLessons(), dtoifsc.getProfessors()));
            lessons[i].setLecturesNumber(retrieveLecturesNumber(lessonId, dtoifsc.getLessons()));
            lessons[i].setMinWorkingDays(retrievePeriodsPerWeek(lessonId, dtoifsc.getLessons()));
            lessons[i].setStudentsNumber(0);

        }
        dtoitc.setLessons(lessons);

        //CONSTRAINTS
        for (Lesson lesson : lessons) {
            String[] professorIds = lesson.getProfessorId();
            for (String professorId : professorIds) {
                for (Teacher iterationTeacher : dtoifsc.getProfessors()) {
                    if (String.valueOf(iterationTeacher.getId()).equals(professorId)) {
                        List<UnavailabilityConstraint> constraintList = convertTimeoffToUnavailability(iterationTeacher.getTimeoff(), String.valueOf(iterationTeacher.getId()));
                        lesson.setConstraints(new UnavailabilityConstraint[constraintList.size()]);
                        lesson.setConstraints(constraintList.toArray(lesson.getConstraints()));
                    }
                }
            }
        }

        //COURSES
        int courseSize = dtoifsc.getClasses().size();
        Course[] courses = new Course[courseSize];
        for (int i = 0; i < courseSize; i++) {
            List<Lesson> lessonList = retrieveCoursesLesson(dtoifsc.getClasses().get(i).getId(), dtoifsc.getLessons(), lessons);
            int size = lessonList.size();
            courses[i] = new Course();
            courses[i].setCourseId(String.valueOf(dtoifsc.getClasses().get(i).getId()));
            courses[i].setCoursesNumber(size);
            courses[i].setShift(convertTimeoffToShift(String.valueOf(dtoifsc.getClasses().get(i).getTimeoff())));

        }


        dtoitc.setCourses(courses);

        return dtoitc;
    }

    private static String retriveLessonsCourse(int lessonId, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == lessonId)
                return String.valueOf(iterationLesson.getClassesId());
        }
        throw new ClassNotFoundException("Lesson não atribuida à um curso");
    }

    private static String[] retrieveProfessorsId(int id, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons, List<Teacher> teachers) throws ClassNotFoundException {
        String[] professorsList = new String[0];
        int count = 0;
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                int[] professorId = iterationLesson.getTeacherId();
                professorsList = new String[professorId.length];
                for (int value : professorId) {
                    for (Teacher iterationTeacher : teachers) {
                        if (iterationTeacher.getId() == value) {
                            professorsList[count] = String.valueOf(iterationTeacher.getId());
                            count++;
                        }
                    }
                }
            }
        }
        if (professorsList.length != 0) {
            return professorsList;
        }
        throw new ClassNotFoundException("Teacher ou Lesson não encontrado");
    }

    private static int retrieveLecturesNumber(int id, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                return iterationLesson.getDurationPeriods();
            }
        }
        throw new ClassNotFoundException("LecturesNumber não encontrado");
    }

    private static int retrievePeriodsPerWeek(int id, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> lessons) throws ClassNotFoundException {
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : lessons) {
            if (iterationLesson.getSubjectId() == id) {
                return iterationLesson.getPeriodsPerWeek() / iterationLesson.getDurationPeriods();
            }
        }
        throw new ClassNotFoundException("LecturesNumber não encontrado");
    }

    private static List<UnavailabilityConstraint> convertTimeoffToUnavailability(String timeoff, String lessonId) {
        List<UnavailabilityConstraint> constraintList = new ArrayList<>();
        String[] days = timeoff.replace(".", "").split(",");
        for (int i = 0; i < days.length - 1; i++) {
            for (int j = 0; j < 12; j++) {
                if (days[i].charAt(j) == '0') {
                    constraintList.add(new UnavailabilityConstraint(lessonId, i, j));
                }
            }
        }
        return constraintList;
    }

    private static byte convertTimeoffToShift(String timeoff) {
        String[] days = timeoff.replace(".", "").split(",");
        if (days[0].charAt(0) == '1')
            return 0;
        else if (days[0].charAt(4) == '1')
            return 1;
        return 2;

    }

    private static List<Lesson> retrieveCoursesLesson(int courseId, List<br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson> IFSCLessons, Lesson[] ITCLesson) {
        List<Lesson> lessonList = new ArrayList<>();
        for (br.edu.ifsc.TimetablingGeneticAlgorithm.domain.ifsc.Lesson iterationLesson : IFSCLessons) {
            if (iterationLesson.getClassesId() == courseId) {
                for (Lesson iterationITCLesson : ITCLesson) {
                    if (iterationITCLesson.getLessonId().equals(String.valueOf(iterationLesson.getSubjectId()))) {
                        lessonList.add(iterationITCLesson);
                    }
                }
            }
        }
        return lessonList;
    }
}