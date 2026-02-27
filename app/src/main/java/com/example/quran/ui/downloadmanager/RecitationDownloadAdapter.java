package com.example.quran.ui.downloadmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quran.R;
import com.example.quran.data.model.Recitation;
import com.example.quran.data.model.Surah;
import com.example.quran.utils.StorageHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying recitations in the Download Manager.
 * Shows surah information with download status and controls.
 */
public class RecitationDownloadAdapter extends RecyclerView.Adapter<RecitationDownloadAdapter.RecitationViewHolder> {

    private List<Recitation> recitations = new ArrayList<>();
    private List<Surah> surahs = new ArrayList<>();
    private OnRecitationActionListener listener;
    private Context context;

    public interface OnRecitationActionListener {
        void onDownloadClick(Recitation recitation, Surah surah);
        void onDeleteClick(Recitation recitation, Surah surah);
        void onCancelClick(Recitation recitation);
    }

    public RecitationDownloadAdapter(Context context, OnRecitationActionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recitation_download, parent, false);
        return new RecitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecitationViewHolder holder, int position) {
        Recitation recitation = recitations.get(position);
        Surah surah = getSurahByNumber(recitation.getSurahNumber());
        if (surah != null) {
            holder.bind(recitation, surah);
        }
    }

    @Override
    public int getItemCount() {
        return recitations.size();
    }

    public void setRecitations(List<Recitation> recitations) {
        this.recitations = recitations;
        notifyDataSetChanged();
    }

    public void setSurahs(List<Surah> surahs) {
        this.surahs = surahs;
        notifyDataSetChanged();
    }

    public void updateRecitation(Recitation updatedRecitation) {
        for (int i = 0; i < recitations.size(); i++) {
            if (recitations.get(i).getId() == updatedRecitation.getId()) {
                recitations.set(i, updatedRecitation);
                notifyItemChanged(i);
                break;
            }
        }
    }

    private Surah getSurahByNumber(int surahNumber) {
        for (Surah surah : surahs) {
            if (surah.getNumber() == surahNumber) {
                return surah;
            }
        }
        return null;
    }

    class RecitationViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSurahNumber;
        private TextView tvSurahNameArabic;
        private TextView tvSurahNameEnglish;
        private ImageButton btnAction;
        private ProgressBar progressBar;
        private TextView tvDownloadInfo;

        public RecitationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSurahNumber = itemView.findViewById(R.id.tvSurahNumber);
            tvSurahNameArabic = itemView.findViewById(R.id.tvSurahNameArabic);
            tvSurahNameEnglish = itemView.findViewById(R.id.tvSurahNameEnglish);
            btnAction = itemView.findViewById(R.id.btnAction);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvDownloadInfo = itemView.findViewById(R.id.tvDownloadInfo);
        }

        public void bind(Recitation recitation, Surah surah) {
            // Set surah information
            tvSurahNumber.setText(String.valueOf(surah.getNumber()));
            tvSurahNameArabic.setText(surah.getNameArabic());
            tvSurahNameEnglish.setText(surah.getNameEnglish());

            // Update UI based on download status
            switch (recitation.getDownloadStatus()) {
                case NOT_DOWNLOADED:
                    showNotDownloaded();
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onDownloadClick(recitation, surah);
                        }
                    });
                    break;

                case DOWNLOADING:
                    showDownloading(recitation.getDownloadProgress());
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onCancelClick(recitation);
                        }
                    });
                    break;

                case DOWNLOADED:
                    showDownloaded(recitation.getFileSize());
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onDeleteClick(recitation, surah);
                        }
                    });
                    break;

                case FAILED:
                    showFailed();
                    btnAction.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onDownloadClick(recitation, surah);
                        }
                    });
                    break;
            }
        }

        private void showNotDownloaded() {
            btnAction.setImageResource(R.drawable.ic_download);
            btnAction.setContentDescription(context.getString(R.string.download));
            progressBar.setVisibility(View.GONE);
            tvDownloadInfo.setVisibility(View.GONE);
        }

        private void showDownloading(int progress) {
            btnAction.setImageResource(R.drawable.ic_close);
            btnAction.setContentDescription(context.getString(R.string.cancel));
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progress);
            tvDownloadInfo.setVisibility(View.VISIBLE);
            tvDownloadInfo.setText(String.format("%d%%", progress));
        }

        private void showDownloaded(long fileSize) {
            btnAction.setImageResource(R.drawable.ic_delete);
            btnAction.setContentDescription(context.getString(R.string.delete));
            progressBar.setVisibility(View.GONE);
            tvDownloadInfo.setVisibility(View.VISIBLE);
            tvDownloadInfo.setText(StorageHelper.formatFileSize(fileSize));
        }

        private void showFailed() {
            btnAction.setImageResource(R.drawable.ic_download);
            btnAction.setContentDescription(context.getString(R.string.download));
            progressBar.setVisibility(View.GONE);
            tvDownloadInfo.setVisibility(View.VISIBLE);
            tvDownloadInfo.setText(context.getString(R.string.download_failed));
        }
    }
}
