import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GraphicsDataWriter {
    public static void main(String[] args) {
        try {
            // Открытие потока для записи в файл
            DataOutputStream out = new DataOutputStream(new FileOutputStream("graphicsData.dat"));

            // Пример данных для графика (координаты точек X и Y)
            Double[][] graphicsData = {
                    {0.0, 1.0},
                    {1.0, 2.0},
                    {2.0, 3.0},
                    {3.0, 4.0},
                    {4.0, 5.0}
            };

            // Запись данных в файл
            for (Double[] point : graphicsData) {
                out.writeDouble(point[0]); // Запись X
                out.writeDouble(point[1]); // Запись Y
            }

            // Закрытие потока
            out.close();

            System.out.println("Данные успешно записаны в файл graphicsData.dat");

        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + e.getMessage());
        }
    }
}
