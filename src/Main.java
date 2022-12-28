import managers.TaskManager;
import managers.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        manager.addTask(new Task("Завтрак", "Приготовить яичницу", "NEW"));
        manager.addTask(new Task("Продукты", "Сходить в магазин", "NEW"));
        manager.addEpic(new Epic("Важный проект", "Разработка приложения", ""));
        manager.addSubtask(new Subtask("Задание", "Изучить ТЗ", "IN_PROGRESS", 3));
        manager.addSubtask(new Subtask("Подход", "Разработать методологию", "NEW", 3));
        manager.addEpic(new Epic("Выпускной", "Сдать экзамены", ""));
        manager.addSubtask(new Subtask("Подготовка", "Прочитать конспекты", "NEW", 6));

        System.out.println("\nСписки всех задач");
        System.out.println(manager.getAllTasks().values());
        System.out.println(manager.getAllEpics().values());
        System.out.println(manager.getAllSubtasks().values());

        System.out.println("\nЗадачи для эпика 3");
        System.out.println(manager.getSubtasksFromEpic(3));
        System.out.println("\nЗадачи для эпика 6");
        System.out.println(manager.getSubtasksFromEpic(6));

        manager.updateTask(1, new Task("Завтрак", "Приготовить яичницу", "DONE"));
        manager.updateTask(2, new Task("Продукты", "Сходить в магазин", "IN_PROGRESS"));
        manager.updateSubtask(4, new Subtask("Задание", "Изучить ТЗ", "DONE", 3));
        manager.updateSubtask(5, new Subtask("Подход", "Разработать методологию", "DONE", 3));
        manager.updateSubtask(7, new Subtask("Подготовка", "Прочитать конспекты", "IN_PROGRESS", 6));

        System.out.println("\nОбновленные списки");
        System.out.println(manager.getAllTasks().values());
        System.out.println(manager.getAllEpics().values());
        System.out.println(manager.getAllSubtasks().values());

        System.out.println("\nЗадачи для эпика 3");
        System.out.println(manager.getSubtasksFromEpic(3));
        System.out.println("\nЗадачи для эпика 6");
        System.out.println(manager.getSubtasksFromEpic(6));

        manager.deleteAnyTask(4);

        System.out.println("\nЗадачи для эпика 3");
        System.out.println(manager.getSubtasksFromEpic(3));
        System.out.println("\nЗадачи для эпика 6");
        System.out.println(manager.getSubtasksFromEpic(6));

        System.out.println(manager.getAnyTask(2));

    }
}