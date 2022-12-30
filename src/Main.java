import managers.*;
import managers.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.addTask(new Task("Завтрак", "Приготовить яичницу", TaskStatus.NEW));
        taskManager.addTask(new Task("Продукты", "Сходить в магазин", TaskStatus.NEW));
        taskManager.addEpic(new Epic("Важный проект", "Разработка приложения", TaskStatus.NEW));
        taskManager.addSubtask(new Subtask("Задание", "Изучить ТЗ", TaskStatus.IN_PROGRESS, 3));
        taskManager.addSubtask(new Subtask("Подход", "Разработать методологию", TaskStatus.NEW, 3));
        taskManager.addEpic(new Epic("Выпускной", "Сдать экзамены", TaskStatus.NEW));
        taskManager.addSubtask(new Subtask("Подготовка", "Прочитать конспекты", TaskStatus.NEW, 6));

        System.out.println("\nСписки всех задач");
        System.out.println(taskManager.getAllTasks().values());
        System.out.println(taskManager.getAllEpics().values());
        System.out.println(taskManager.getAllSubtasks().values());

        System.out.println("\nЗадачи для эпика 3");
        System.out.println(taskManager.getSubtasksFromEpic(3));
        System.out.println("\nЗадачи для эпика 6");
        System.out.println(taskManager.getSubtasksFromEpic(6));

        taskManager.updateTask(1, new Task("Завтрак", "Приготовить яичницу", TaskStatus.DONE));
        taskManager.updateTask(2, new Task("Продукты", "Сходить в магазин", TaskStatus.IN_PROGRESS));
        taskManager.updateSubtask(4, new Subtask("Задание", "Изучить ТЗ", TaskStatus.DONE, 3));
        taskManager.updateSubtask(5, new Subtask("Подход", "Разработать методологию", TaskStatus.DONE, 3));
        taskManager.updateSubtask(7, new Subtask("Подготовка", "Прочитать конспекты", TaskStatus.IN_PROGRESS, 6));

        System.out.println("\nОбновленные списки");
        System.out.println(taskManager.getAllTasks().values());
        System.out.println(taskManager.getAllEpics().values());
        System.out.println(taskManager.getAllSubtasks().values());

        System.out.println("\nЗадачи для эпика 3");
        System.out.println(taskManager.getSubtasksFromEpic(3));
        System.out.println("\nЗадачи для эпика 6");
        System.out.println(taskManager.getSubtasksFromEpic(6));

        taskManager.deleteAnyTask(4);

        System.out.println("\nЗадачи для эпика 3");
        System.out.println(taskManager.getSubtasksFromEpic(3));
        System.out.println("\nЗадачи для эпика 6");
        System.out.println(taskManager.getSubtasksFromEpic(6));

        taskManager.getAnyTask(1);
        taskManager.getAnyTask(2);
        taskManager.getAnyTask(3);
        taskManager.getAnyTask(2);
        taskManager.getAnyTask(5);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(1);
        taskManager.getAnyTask(3);

        System.out.println("\nИстория просмотров последних 10 задач");
        System.out.println(taskManager.getHistory());

    }
}