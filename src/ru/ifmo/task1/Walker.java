package ru.ifmo.task1;

/**
 * Created by Nechaev Mikhail
 * Since 09/09/2018.
 *
 * При выполнение задания следует обратить внимание на
 *  java.nio.file.Files и java.nio.file.Paths,
 *  а также корректность использование try-with-resources.
 *
 * https://docs.oracle.com/javase/tutorial/essential/io/index.html
 */
public class Walker {

    public static void main(String[] args) {
        /**
         * todo: проверить аргументы на корректность и передать дальше если всё ок
         * args[0] - входной файл
         * args[1] - выходной файл
         */
        new Walker().run(/* todo */);
    }

    //todo: дополнить сигнатуру метода входными файлами, полученными в main
    // При необходимости создайте дополнительные классы
    // hint: Вывести результат хеша в правильном формате - String.format("%08x", hash)
    private void run() {
        System.out.println("run!");
    }
}