package fortume.algorithm;

public class ParabolaMath {

    public static final double EPSILON = Double.MIN_VALUE * 1E100;

    public static double EvalParabola(double focusX, double focusY, double directrix, double x) {
        return .5 * ((x - focusX) * (x - focusX) / (focusY - directrix) + focusY + directrix);
    }

    //gives the intersect point such that parabola 1 will be on top of parabola 2 slightly before the intersect

    public static double IntersectParabolaX(double focus1X, double focus1Y, double focus2X, double focus2Y,
                                            double directrix) {
        //admittedly this is pure voodoo.
        //there is attached documentation for this function
        return ApproxEqual(focus1Y, focus2Y) ? (focus1X + focus2X) / 2
                : (focus1X * (directrix - focus2Y) + focus2X * (focus1Y - directrix) +
                Math.sqrt((directrix - focus1Y) * (directrix - focus2Y) *
                        ((focus1X - focus2X) * (focus1X - focus2X) +
                                (focus1Y - focus2Y) * (focus1Y - focus2Y))
                )
        ) / (focus1Y - focus2Y);
    }

    public static boolean ApproxEqual(double value1, double value2) {
        return Math.abs(value1 - value2) <= EPSILON;
    }

    public static boolean ApproxGreaterThanOrEqualTo(double value1, double value2) {
        return value1 > value2 || ApproxEqual(value1, value2);
    }

    public static boolean ApproxLessThanOrEqualTo(double value1, double value2) {
        return value1 < value2 || ApproxEqual(value1, value2);
    }
}

