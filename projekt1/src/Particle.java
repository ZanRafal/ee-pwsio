class Particle {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private final int id;

    public Particle(double x, double y, double vx, double vy, int id) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.id = id;
    }

    public void update() {
        x += vx;
        y += vy;

        if (x <= 0 || x >= 1000) {
            vx = -vx;
        }
        if (y <= 0 || y >= 1000) {
            vy = -vy;
        }
    }

    @Override
    public String toString() {
        return "Cząstka " + id + " [x=" + x + ", y=" + y + "]";
    }
}
