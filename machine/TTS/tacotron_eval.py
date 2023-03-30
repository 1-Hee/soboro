import numpy as np
import torch
import tensorflow as tf
from hparams import hparams
from models import create_model
from util.text import text_to_sequence, sequence_to_text

class Synthesizer:
    def load(self, checkpoint_path, model_name='tacotron'):
        print('Constructing model: %s' % model_name)
        inputs = tf.placeholder(tf.int32, [1, None], 'inputs')
        input_lengths = tf.placeholder(tf.int32, [1], 'input_lengths')
        with tf.variable_scope('model') as scope:
            
            self.model = create_model(model_name, hparams)
            self.model.initialize(inputs, input_lengths)
            self.alignments = self.model.alignments[0]
            self.inputs = self.model.inputs[0]
            self.mel_outputs = self.model.mel_outputs[0]

        print('Loading checkpoint: %s' % checkpoint_path)
        self.session = tf.Session()
        self.session.run(tf.global_variables_initializer())
        saver = tf.train.Saver()
        saver.restore(self.session, checkpoint_path)

    def synthesize(self, text):
        seq = text_to_sequence(text)
        feed_dict = {
            self.model.inputs: [np.asarray(seq, dtype=np.int32)],
            self.model.input_lengths: np.asarray([len(seq)], dtype=np.int32)
        }
        input_seq, _, mel = self.session.run([self.inputs, self.alignments, self.mel_outputs], feed_dict=feed_dict)
        input_seq = sequence_to_text(input_seq)
        mel_spectrogram = torch.from_numpy(mel.T)
        return mel_spectrogram