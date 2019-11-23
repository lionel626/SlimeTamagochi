package com.example.slimetamagochi;

import java.util.Random;

public class Tamagochi {


    int healCost = 10;
    int feedCost = 5;
    int powerCost = 20;

    Random rand;
    int score;

    float maxHealth;
    float maxPower;
    float maxHunger;

    float health;
    float power;
    float hunger;
    int age;
    int death;
    boolean alive;

    int tick;



    public Tamagochi(int death) {
        this.age = 0;
        this.alive = true;
        this.death = death;

        this.maxHealth = 100;
        this.maxPower = 100;
        this.maxHunger = 100;

        this.health = 100;
        this.power = 100;
        this.hunger = 100;
        this.score = 10;


        rand = new Random();

    }

    public Tamagochi(int death, int age, float l,float h, float p, int s) {
        this.age = age;
        this.alive = true;
        this.death = death;

        this.maxHealth = 100;
        this.maxPower = 100;
        this.maxHunger = 100;

        this.health = l;
        this.power = p;
        this.hunger = h;
        this.score = s;


        rand = new Random();

    }

    public void update() {
        loseHealth();
        loseHunger();
        losePower();
        statcheck();
    }

    public void loseHealth() {
        if(hunger <=0 && power<=0) {
            health -=4;
        } else if (hunger<=0) {
            health--;
        }
    }

    public void losePower() {
        if(hunger<=0) {
            power-=4;
        }

    }

    public void loseHunger() {
        if(tick%2 == 0) {
            hunger--;
        }
    }

    public void statcheck() {
        if (power <= 0) {
            power = 0;
        }
        if (health <= 0) {
            health = 0;
            alive = false;
        }
        if (hunger <= 0) {
            hunger = 0;
        }
        if(age >= death) {
            alive = false;
        }
    }

    public void addscore(int s) {
        score += s;
    }

    public void heal() {
        float f = 10;
        if(!(score - healCost < 0)) {
            if(rand.nextDouble() < 0.10) {
                f += rand.nextInt(8)+2;
            }
            score -= healCost;
        }
        this.health += f;
    }

    public void feed() {
        float f = 5;
        if (!(score - feedCost < 0)) {
            if (rand.nextDouble() < 0.15) {
                f += rand.nextInt(8) + 2;
            }
            score -= feedCost;
        }
        this.hunger += f;
    }

    public void feedpower() {
        float f = 10;
        if (!(score - powerCost < 0)) {
            if (rand.nextDouble() < 0.20) {
                f += rand.nextInt(8) + 2;
            }
            score -= powerCost;
        }
        this.power += f;
    }
}
