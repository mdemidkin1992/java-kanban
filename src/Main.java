import managers.*;
import managers.tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        taskManager.addTask(new Task("Задача 1", "Задача 1", TaskStatus.NEW));
        taskManager.addTask(new Task("Задача 2", "Задача 2", TaskStatus.IN_PROGRESS));
        taskManager.addEpic(new Epic("Эпик 1", "Эпик 1", TaskStatus.NEW));
        taskManager.addSubtask(new Subtask("Подзадача 1", "Подзадача 1", TaskStatus.NEW, 3));
        taskManager.addSubtask(new Subtask("Подзадача 2", "Подзадача 2", TaskStatus.IN_PROGRESS, 3));
        taskManager.addSubtask(new Subtask("Подзадача 3", "Подзадача 3", TaskStatus.DONE, 3));
        taskManager.addEpic(new Epic("Эпик 2", "Эпик 1", TaskStatus.IN_PROGRESS));

        System.out.println("\nСписки всех задач");
        System.out.println(taskManager.getAllTasks().values());
        System.out.println(taskManager.getAllEpics().values());
        System.out.println(taskManager.getAllSubtasks().values());

        System.out.println("\nПроверка истории простотров 1");
        taskManager.getAnyTask(1);
        taskManager.getAnyTask(2);
        taskManager.getAnyTask(3);
        System.out.println(taskManager.getHistory());

        System.out.println("\nПроверка истории простотров 2");
        taskManager.getAnyTask(2);
        taskManager.getAnyTask(5);
        taskManager.getAnyTask(6);
        System.out.println(taskManager.getHistory());

        System.out.println("\nПроверка истории простотров 3");
        taskManager.getAnyTask(2);
        taskManager.getAnyTask(6);
        taskManager.getAnyTask(7);
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем задачу -> выводим историю");
        taskManager.deleteAnyTask(2);
        System.out.println(taskManager.getHistory());

        System.out.println("\nУдаляем эпик с подзадачами -> выводим историю");
        taskManager.deleteAnyTask(3);
        System.out.println(taskManager.getHistory());
    }
}