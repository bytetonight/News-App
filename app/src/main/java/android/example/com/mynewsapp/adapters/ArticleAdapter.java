package android.example.com.mynewsapp.adapters;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import android.example.com.mynewsapp.R;
import android.example.com.mynewsapp.databinding.ArticleListItemBinding;
import android.example.com.mynewsapp.models.Article;
import android.example.com.mynewsapp.utils.Handlers;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> items;


    public ArticleAdapter(List<Article> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // was using ViewDataBinding binding but you can't assign handlers to the base
        ArticleListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.article_list_item, parent, false);
        binding.setHandlers(new Handlers()); //use my handlers class

        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        /**
         * @param binding of type ViewDataBinding which is an
         *                abstract Base Class for generated binding classes
         */
        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj) {
            binding.setVariable(android.example.com.mynewsapp.BR.article, obj);
            binding.executePendingBindings();
        }
    }

}
