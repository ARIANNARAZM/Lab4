import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GraphicsDisplay extends JPanel {
    private Double[][] graphicsData;  // Данные для графика
    private boolean showAxis = true;  // Показывать оси
    private boolean showMarkers = true;  // Показывать маркеры
    private double minX, maxX, minY, maxY;  // Границы координат
    private double scale;  // Масштаб
    private BasicStroke graphicsStroke, axisStroke, markerStroke;  // Строки для рисования
    private Font axisFont;  // Шрифт для подписей осей

    public GraphicsDisplay() {
        setBackground(Color.WHITE);  // Белый фон

        // Настройка перьев и шрифтов
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f,
                new float[]{5, 5, 10, 5, 5,5,20, 5, 10, 5, 5,5}, 0.0f);
        axisStroke = new BasicStroke(2.0f);
        markerStroke = new BasicStroke(1.0f);
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    // Метод для отображения графика
    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();  // Перерисовать компонент
    }

    // Методы для настройки отображения осей и маркеров
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    // Переопределяем paintComponent для рисования
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  // Вызов родительского метода

        if (graphicsData == null || graphicsData.length == 0) return;

        // Определение диапазона данных по осям
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) minY = graphicsData[i][1];
            if (graphicsData[i][1] > maxY) maxY = graphicsData[i][1];
        }

        // Уменьшаем размер графика относительно окна (с добавлением отступов)
        double scaleX = (getWidth() - 10) / (maxX - minX);  // Отступы по оси X
        double scaleY = (getHeight() - 10) / (maxY - minY);  // Отступы по оси Y
        scale = Math.min(scaleX, scaleY) * 0.6;  // Уменьшаем график на 80%

        // Корректировка границ для симметричного отображения
        double xIncrement = (getWidth() - 40) / scale - (maxX - minX);
        double yIncrement = (getHeight() - 40) / scale - (maxY - minY);

        minX -= xIncrement / 2;
        maxX += xIncrement / 2;
        minY -= yIncrement / 2;
        maxY += yIncrement / 2;

        // Подготовка для рисования
        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();

        // Рисуем название графика
        canvas.setFont(new Font("Serif", Font.BOLD, 24));
        canvas.drawString("График функции ", getWidth() / 2 - 10, 40);

        // Рисуем оси
        if (showAxis) paintAxis(canvas);

        // Рисуем график
        paintGraphics(canvas);

        // Рисуем маркеры
        if (showMarkers) paintMarkers(canvas);

        // Восстановление старых параметров
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    // Отображение графика
    protected void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.PINK);

        GeneralPath path = new GeneralPath();
        for (int i = 0; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            if (i > 0) {
                path.lineTo(point.getX(), point.getY());
            } else {
                path.moveTo(point.getX(), point.getY());
            }
        }

        canvas.draw(path);
    }

    protected void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);

        for (Double[] point : graphicsData) {
            Point2D.Double center = xyToPoint(point[0], point[1]);

            // Проверка, является ли целая часть значения Y четным числом
            boolean isEven = isEvenInteger(point[1]);
            canvas.setColor(isEven ? Color.GREEN : Color.RED); // Зеленый для четных целых чисел, красный для остальных

            // Размеры треугольников
            int size = 10; // Длина стороны треугольника

            // Создаем 4 треугольника
            for (int i = 0; i < 4; i++) {
                GeneralPath triangle = new GeneralPath();

                switch (i) {
                    case 0: // Вверх
                        triangle.moveTo(center.getX(), center.getY() - size);
                        triangle.lineTo(center.getX() - size / 2, center.getY());
                        triangle.lineTo(center.getX() + size / 2, center.getY());
                        break;
                    case 1: // Вниз
                        triangle.moveTo(center.getX(), center.getY() + size);
                        triangle.lineTo(center.getX() - size / 2, center.getY());
                        triangle.lineTo(center.getX() + size / 2, center.getY());
                        break;
                    case 2: // Влево
                        triangle.moveTo(center.getX() - size, center.getY());
                        triangle.lineTo(center.getX(), center.getY() - size / 2);
                        triangle.lineTo(center.getX(), center.getY() + size / 2);
                        break;
                    case 3: // Вправо
                        triangle.moveTo(center.getX() + size, center.getY());
                        triangle.lineTo(center.getX(), center.getY() - size / 2);
                        triangle.lineTo(center.getX(), center.getY() + size / 2);
                        break;
                }

                triangle.closePath(); // Замыкаем треугольник

                // Рисуем и закрашиваем треугольник
                canvas.draw(triangle);
                canvas.fill(triangle);
            }
        }
    }

    // Метод для проверки, является ли целая часть числа четным числом
    private boolean isEvenInteger(double value) {
        int intValue = (int) value;  // Получаем целую часть числа
        return intValue % 2 == 0;  // Проверяем, четное ли оно
    }


    // Отображение осей
    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);

        FontRenderContext context = canvas.getFontRenderContext();

        // Ось Y
        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));

            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()));
        }

        // Ось X
        if (minY <= 0.0 && maxY >= 0.0 || minX <= 0.0 && maxX >= 0.0) {
            // Если ось X пересекает окно, рисуем её
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));

            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));
        }

        if (minX <= 0.0 && maxX >= 0.0 && minY <= 0.0 && maxY >= 0.0) {
            Point2D.Double zeroPoint = xyToPoint(0, 0);
            Rectangle2D bounds = axisFont.getStringBounds("0", context);
            canvas.drawString("0",
                    (float) (zeroPoint.getX() - bounds.getWidth() / 2),
                    (float) (zeroPoint.getY() + bounds.getHeight()));
        }
    }

    // Преобразование координат
    protected Point2D.Double xyToPoint(double x, double y) {
        // Масштабирование и добавление отступов для центровки графика
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale + getWidth() / 2 - (maxX - minX) * scale / 2,
                deltaY * scale + getHeight() / 2 - (maxY - minY) * scale / 2);
    }

    // Сдвиг точки
    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}