package com.example.comprasegura.ui.catalogo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.comprasegura.R
import com.example.comprasegura.data.remote.dto.ProductoDto
import com.example.comprasegura.databinding.ItemProductoBinding
import com.example.comprasegura.util.formatearMoneda

class ProductoAdapter(
    private val alSeleccionar: (ProductoDto) -> Unit,
) : ListAdapter<ProductoDto, ProductoAdapter.VistaProducto>(Comparador) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaProducto {
        val binding = ItemProductoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VistaProducto(binding)
    }

    override fun onBindViewHolder(holder: VistaProducto, position: Int) {
        holder.mostrar(getItem(position))
    }

    inner class VistaProducto(private val binding: ItemProductoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun mostrar(producto: ProductoDto) {
            binding.titulo.text = producto.titulo
            binding.precio.text = formatearMoneda(producto.precio)
            binding.vendedor.text = binding.root.context.getString(
                R.string.catalogo_vendedor,
                producto.vendedor?.nombre_comercial ?: "-",
            )
            binding.root.setOnClickListener { alSeleccionar(producto) }
        }
    }

    private object Comparador : DiffUtil.ItemCallback<ProductoDto>() {
        override fun areItemsTheSame(a: ProductoDto, b: ProductoDto) = a.id == b.id
        override fun areContentsTheSame(a: ProductoDto, b: ProductoDto) = a == b
    }
}
