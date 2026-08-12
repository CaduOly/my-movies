package org.example.mymovies.dao;

import org.example.mymovies.model.MediaItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MySqlMediaItemDAOTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MySqlMediaItemDAO dao;

    @BeforeEach
    public void setUp() throws Exception {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
        
        dao = new MySqlMediaItemDAO(mockConnection);
    }

    @Test
    public void testInsertUsesPreparedStatement() throws Exception {
        MediaItem item = new MediaItem("Inception", "A dream within a dream", org.example.mymovies.model.MediaType.MOVIE, 2010, "Christopher Nolan", "Sci-Fi");
        
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        dao.insert(item);
        
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture(), eq(Statement.RETURN_GENERATED_KEYS));
        
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("?"), "SQL must use placeholders (?) to prevent SQL injection");
        
        verify(mockPreparedStatement).setString(1, "Inception");
        verify(mockPreparedStatement).setString(2, "A dream within a dream");
        verify(mockPreparedStatement).setString(3, "MOVIE");
        verify(mockPreparedStatement).setInt(4, 2010);
        verify(mockPreparedStatement).setString(5, "Christopher Nolan");
        verify(mockPreparedStatement).setString(6, "Sci-Fi");
        
        assertEquals(1L, item.getId());
    }

    @Test
    public void testInsertWithNullReleaseYear() throws Exception {
        MediaItem item = new MediaItem("Old Movie", "No year", org.example.mymovies.model.MediaType.MOVIE, null, "Unknown", "Classic");
        
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(2L);

        dao.insert(item);
        
        verify(mockPreparedStatement).setNull(4, java.sql.Types.INTEGER);
    }
    
    @Test
    public void testMapRowHandlesNullReleaseYear() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getObject("release_year")).thenReturn(null);
        when(mockResultSet.getString("title")).thenReturn("Title");
        when(mockResultSet.getString("description")).thenReturn("Desc");
        when(mockResultSet.getString("media_type")).thenReturn("MOVIE");
        when(mockResultSet.getString("author_director")).thenReturn("Author");
        when(mockResultSet.getString("genre")).thenReturn("Genre");
        when(mockResultSet.getLong("id")).thenReturn(1L);

        java.util.List<MediaItem> results = dao.searchByTerm("Title");
        assertEquals(1, results.size());
        assertNull(results.get(0).getReleaseYear());
    }
    
    @Test
    public void testSearchByTermUsesPreparedStatement() throws Exception {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        dao.searchByTerm("dream");

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());

        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("?"), "SQL must use placeholders (?) to prevent SQL injection");

        verify(mockPreparedStatement).setString(1, "%dream%");
        verify(mockPreparedStatement).setString(2, "%dream%");
    }
}
