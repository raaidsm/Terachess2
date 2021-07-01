class Square {
    constructor(squareName) {
        this.squareName = squareName;
    }

    getSquareNameLetter() {
        return this.squareName.substring(0, 1);
    }
    getSquareNameNumber() {
        return parseInt(this.squareName.substring(1));
    }
}

export default Square;