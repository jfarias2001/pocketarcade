package com.example.pocketarcade;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketarcade.databinding.ItemPalavraBinding;

import java.util.List;

// o Adapter liga a lista de itens com o RecyclerView
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<Item> itens;

    public ItemAdapter(List<Item> itens) {
        this.itens = itens;
    }

    // cria a view de um item (so e chamado quando o RecyclerView precisa de uma view nova)
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPalavraBinding itemBinding = ItemPalavraBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemViewHolder(itemBinding);
    }

    // coloca os dados do item na view (chamado toda vez que uma view e reaproveitada)
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itens.get(position);
        holder.itemBinding.textPalavra.setText(item.getPalavra());
        holder.itemBinding.textCategoria.setText(item.getCategoria());
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    // o ViewHolder guarda as views de um item para nao precisar criar de novo
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemPalavraBinding itemBinding;

        public ItemViewHolder(ItemPalavraBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;

            // clique longo exclui o item
            itemBinding.getRoot().setOnLongClickListener(v -> {
                int posicao = getBindingAdapterPosition();
                if (posicao == RecyclerView.NO_POSITION) {
                    return false;
                }
                Item removido = itens.remove(posicao);
                notifyItemRemoved(posicao);
                Toast.makeText(v.getContext(),
                        v.getContext().getString(R.string.aviso_item_removido, removido.getPalavra()),
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}
