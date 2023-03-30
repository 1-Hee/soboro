import tensorflow as tf
tf.compat.v1.logging.set_verbosity(tf.compat.v1.logging.ERROR)

# Default hyperparameters:
hparams = tf.contrib.training.HParams(

  # Audio:
  num_mels=80,
  num_freq=513,
  n_fft=1024,
  sample_rate=22050,
  win_length=1024,
  hop_length=256,
  preemphasis=0.97,
  min_level_db=-100,
  ref_level_db=20,

  # Model:
  outputs_per_step=5,
  len_symbols=70,
  embed_depth=256,
  prenet_depths=[256, 128],
  encoder_depth=256,
  postnet_depth=256,
  attention_depth=256,
  decoder_depth=256,

  # Training:
  batch_size=32,
  adam_beta1=0.9,
  adam_beta2=0.999,
  initial_learning_rate=0.002,
  decay_learning_rate=True,

  # Eval:
  max_iters=200,
  griffin_lim_iters=60,
  power=1.5,
)