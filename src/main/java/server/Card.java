package server;

import java.io.Serializable;

enum CardsPoints
{
    AS(11),
    KING(4),
    QUEEN(3),
    TEN(10),
    NINE(0),
    SEVEN(0),
    EIGHT(0),
    VALET(2);

    public void setCards(int points){
        e_points = points;
    }

    public int getCards(){
        return e_points;
    }
    private CardsPoints(int points){
        e_points = points;
    }
    private int e_points;
}

public class Card implements Serializable
{
    private CardsColors _color;
    private CardsPoints _points;

    public Card(CardsPoints point, CardsColors color) {
        _color = color;
        _points = point;
    }


    public CardsPoints get_points() {
        return _points;
    }

    public void set_points(CardsPoints _points) {
        this._points = _points;
    }

    public CardsColors get_color() {
        return _color;
    }

    public void set_color(CardsColors _color) {
        this._color = _color;
    }
}