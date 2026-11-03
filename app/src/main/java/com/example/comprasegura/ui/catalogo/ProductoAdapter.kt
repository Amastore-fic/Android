package com.example.comprasegura.ui.catalogo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.comprasegura.data.remote.dto.ProductoDto
import com.example.comprasegura.databinding.ItemProductoBinding
import com.example.comprasegura.util.formatearMoneda
import java.util.Locale

class ProductoAdapter(
    private val alSeleccionar: (ProductoDto) -> Unit
) : ListAdapter<ProductoDto, ProductoAdapter.VistaProducto>(Comparador) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VistaProducto {
        val binding = ItemProductoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VistaProducto(binding)
    }

    override fun onBindViewHolder(
        holder: VistaProducto,
        position: Int
    ) {
        holder.mostrar(getItem(position))
    }

    inner class VistaProducto(
        private val binding: ItemProductoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun mostrar(producto: ProductoDto) {

            binding.titulo.text = producto.titulo

            binding.precio.text =
                formatearMoneda(producto.precio)

            binding.vendedor.text =
                producto.vendedor?.nombre_comercial ?: ""

            val puntuacion =
                producto.calificaciones_avg_puntuacion
                    ?.toDoubleOrNull()

            val cantidad =
                producto.calificaciones_count ?: 0

            if (puntuacion != null && cantidad > 0) {
                binding.calificacion.text =
                    "★ %.1f (%d)".format(
                        Locale.getDefault(),
                        puntuacion,
                        cantidad
                    )

                binding.calificacion.visibility = View.VISIBLE
            } else {
                binding.calificacion.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                alSeleccionar(producto)
            }
        }
    }

    private object Comparador :
        DiffUtil.ItemCallback<ProductoDto>() {

        override fun areItemsTheSame(
            anterior: ProductoDto,
            nuevo: ProductoDto
        ): Boolean {
            return anterior.id == nuevo.id
        }

        override fun areContentsTheSame(
            anterior: ProductoDto,
            nuevo: ProductoDto
        ): Boolean {
            return anterior == nuevo
        }
    }
}
