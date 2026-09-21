import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var students = List.of(
            new Student("Ana", "DAW", true),
            new Student("Pedro", "DAM", false),
            new Student("Lucía", "DAW", true)
        );

        var activeNames = students.stream()
            .filter(Student::active)
            .map(Student::name)
            .sorted()
            .toList();

        Map<String, List<Student>> byCourse = students.stream()
            .collect(Collectors.groupingBy(Student::course));

        Optional<Student> firstActive = students.stream()
            .filter(Student::active)
            .findFirst();

        System.out.println(activeNames);
        System.out.println(byCourse.keySet());
        System.out.println(firstActive.map(Student::name).orElse("none"));
    }
}
