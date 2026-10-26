package com.cirms.service;

import com.cirms.dto.MatchDTO;
import com.cirms.entity.Item;

import java.util.List;

/**
 * Contract for the smart attribute-based item matching service.
 *
 * For a given item the algorithm scans the opposite-type bucket
 * (LOST→FOUND or FOUND→LOST) and returns a ranked list of candidates
 * sorted by composite match score descending.
 */
public interface MatchingService {

    /**
     * Find the top potential matches for the given item.
     *
     * @param sourceItem  the item to match against
     * @param maxResults  maximum number of results to return
     * @return            ranked list of MatchDTO, highest score first
     */
    List<MatchDTO> findMatches(Item sourceItem, int maxResults);

    /**
     * Run matching for a newly created item and fire in-app notifications
     * to the reporter when high-confidence matches are found.
     *
     * @param newItem   the newly persisted item
     */
    void triggerMatchingAndNotify(Item newItem);
}
