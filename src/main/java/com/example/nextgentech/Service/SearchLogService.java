package com.example.nextgentech.Service;

import com.example.nextgentech.Model.SearchLog;
import com.example.nextgentech.Model.User;
import com.example.nextgentech.Repository.SearchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchLogService {

    @Autowired
    private SearchLogRepository searchLogRepository;

    public void saveSearchLog(User user, String query) {
        SearchLog searchLog = new SearchLog();
        searchLog.setUser(user);
        searchLog.setSearchQuery(query);
        searchLogRepository.save(searchLog);
    }
    public List<SearchLog> getRecentSearches(User user) {
        return searchLogRepository.findTop5ByUserOrderByCreatedAtDesc(user);
    }

    public List<String> getRecentSearchQueries(Long userId) {
        return searchLogRepository.findTop5SearchQueriesByUserId(userId);
    }
}
