package ani.saikou.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ani.saikou.BottomSheetDialogFragment
import ani.saikou.R
import ani.saikou.databinding.BottomSheetDevelopersBinding

class ForksDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetDevelopersBinding? = null
    private val binding get() = _binding!!

    private val developers = arrayOf(
        Developer("Saikou TV","https://avatars.githubusercontent.com/u/32782575?s=120&v=4","Nanoc6","https://github.com/Nanoc6/SaikouTV"),
        Developer("Saikou SP (Spanish)","https://avatars.githubusercontent.com/u/80992641?s=120&v=4",".DiegoPYL","https://github.com/Diegopyl1209/saikouSP"),
        Developer("Saikou IT (Italiano)","https://avatars.githubusercontent.com/u/38143733?s=120&v=4","AntonyDP","https://github.com/antonydp/saikou-italiano"),
        Developer("Saikou IN (Indonesian)","https://avatars.githubusercontent.com/u/69040506?s=120&v=4","Brahmkshatriya","https://github.com/saikou-app/saikou-in"),
        Developer("Saikou VN (Vietnamese)","https://avatars.githubusercontent.com/u/68330291?s=120&v=4","Vu Nguyen","https://github.com/hoangvu12/saikou")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetDevelopersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.devsTitle.setText(R.string.forks)
        binding.devsRecyclerView.adapter = DevelopersAdapter(developers)
        binding.devsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
