import {boardLength, letters} from "./utils.js";

class Point {
    constructor(squareName) {
        this.x = 0;
        this.y = 0;
        if (squareName == null) return;

        let letterRep = squareName.substring(0, 1);
        let numberRep = parseInt(squareName.substring(1));
        this.x = letters.indexOf(letterRep) + 1;
        this.y = numberRep;
    }

    static getHorizontalDistOnSamePlane(squareName1, squareName2) {
        let point1 = new Point(squareName1);
        let point2 = new Point(squareName2);

        if (point1.y === point2.y) {
            return point2.x - point1.x;
        }
        else return null
    }
    static getVerticalDist(squareName1, squareName2) {
        let point1 = new Point(squareName1);
        let point2 = new Point(squareName2);

        return point2.y - point1.y;
    }
    static getHorizontalDist(squareName1, squareName2) {
        let point1 = new Point(squareName1);
        let point2 = new Point(squareName2);

        return point2.x - point1.x;
    }

    getRelativeSquareName(shiftX, shiftY) {
        if (this.x === 0 && this.y === 0) return null;
        let tempX = this.x + shiftX;
        let numberRep = this.y + shiftY;

        if (boardLength < tempX || boardLength < numberRep) return null;
        if (tempX < 1 || numberRep < 1) return null;

        return letters[tempX - 1] + numberRep;
    }
}

export default Point;