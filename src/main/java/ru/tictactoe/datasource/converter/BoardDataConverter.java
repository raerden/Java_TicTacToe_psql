package ru.tictactoe.datasource.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.tictactoe.datasource.model.BoardData;
/*
Преобразует матрицу в строку, для сохранения в БД, и наоборот.
*/

@Converter(autoApply = true)  // автоматически применяется для всех полей типа BoardData
public class BoardDataConverter implements AttributeConverter<BoardData, String> {

    private static final int SIZE = 3;

    @Override
    public String convertToDatabaseColumn(BoardData boardData) {
        if (boardData == null || boardData.getMatrix() == null) {
            return null;
        }

        int[][] matrix = boardData.getMatrix();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(matrix[i][j]);
            }
        }

        return sb.toString();
    }

    @Override
    public BoardData convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() != SIZE * SIZE) {
            return new BoardData(new int[SIZE][SIZE]);
        }

        int[][] matrix = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                char c = dbData.charAt(i * SIZE + j);
                matrix[i][j] = Character.getNumericValue(c);
            }
        }

        return new BoardData(matrix);
    }
}