package com.jumadi.moviecatalogue.ui.home;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.jumadi.moviecatalogue.data.api.MovieRepository;
import com.jumadi.moviecatalogue.data.api.repository.NetworkState;
import com.jumadi.moviecatalogue.data.entitiy.MovieEntity;
import com.jumadi.moviecatalogue.in.Listing;

import javax.inject.Inject;

import static com.jumadi.moviecatalogue.data.api.ApiService.API_KEY;

public class DiscoverViewModel extends ViewModel {

    private final MovieRepository movieRepository;

    private final MutableLiveData<String> apiKey = new MutableLiveData<>();
    private final LiveData<Listing<MovieEntity>> result = Transformations.map(apiKey, new Function<String, Listing<MovieEntity>>() {
        @Override
        public Listing<MovieEntity> apply(String input) {
            return movieRepository.discoverSource(input);
        }
    });

    private LiveData<PagedList<MovieEntity>> movies;
    private LiveData<NetworkState> networkState;
    private LiveData<NetworkState> initialLoad;

    @Inject
    DiscoverViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        if (movies == null)
            initializePageList();
    }

    private void initializePageList() {

        movies = Transformations.switchMap(result, Listing::getPageList);
        initialLoad = Transformations.switchMap(result, Listing::getInitialLoad);
        networkState = Transformations.switchMap(result, Listing::getNetworkState);
    }

    public LiveData<PagedList<MovieEntity>> getMovies() {
        return movies;
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<NetworkState> getInitialLoad() {
        return initialLoad;
    }

    void retry() {
        if (result.getValue() != null)
            result.getValue().retry();
    }

    void refrash() {
        if (result.getValue() !=null)
            result.getValue().refresh();
    }

    public DiscoverViewModel setApiKey() {
        this.apiKey.setValue(API_KEY);
        return this;
    }
}