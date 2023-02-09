package managers.historymanagers;

import managers.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodeMap = new HashMap<>();

    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
        }
    }

    private void linkLast(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(oldTail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.setNext(newTail);
        }
        nodeMap.put(task.getId(), newTail);
    }

    private void removeNode(Node<Task> node) {
        if (head != null) {
            Node<Task> currentNode = head;
            while (currentNode.getNext() != null) {
                if (currentNode.getData().equals(node.getData())) {
                    if (currentNode == head) {
                        head = currentNode.getNext();
                    } else {
                        currentNode.getPrev().setNext(currentNode.getNext());
                    }
                    currentNode.getNext().setPrev(currentNode.getPrev());
                }
                currentNode = currentNode.getNext();
            }
            if (currentNode.getData().equals(node.getData())) {
                tail = currentNode.getPrev();
                currentNode.getPrev().setNext(null);
            }
        }
    }

    private List<Task> getTasks() {
        List<Task> newList = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            newList.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return newList;
    }

    private class Node<T> {
        private final T data;
        private Node<T> next;
        private Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        public T getData() {
            return data;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }
    }
}